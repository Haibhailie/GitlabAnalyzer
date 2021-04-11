package ca.sfu.orcus.gitlabanalyzer.file;

import ca.sfu.orcus.gitlabanalyzer.utils.Diff.LOCDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.Scores;
import com.google.gson.Gson;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FileRepository {
    private static final Gson gson = new Gson();

    private enum File {
        documentId("_id"),
        files("files"),
        name("name"),
        extension("extension"),
        fileScore("fileScore"),
        linesOfCodeChanges("linesOfCodeChanges"),
        isIgnored("isIgnored");

        public String key;

        File(String key) {
            this.key = key;
        }
    }

    public List<FileDto> getFilesFromCache(Document doc) {
        List<Document> fileDocuments = doc.getList(File.files.key, Document.class);
        List<FileDto> files = new ArrayList<>();
        for (Document presentDocument : fileDocuments) {
            files.add(getFileFromDocument(presentDocument));
        }
        return files;
    }

    private FileDto getFileFromDocument(Document doc) {
        FileDto fileDto = new FileDto(doc.getString(File.name.key));
        fileDto.setExtension(doc.getString(File.extension.key));
        fileDto.setTotalScore(gson.fromJson(doc.getString(File.fileScore.key), Scores.class));
        fileDto.setLinesOfCodeChanges(gson.fromJson(doc.getString(File.linesOfCodeChanges.key), LOCDto.class));
        fileDto.setIgnored(doc.getBoolean(File.isIgnored));
        return fileDto;
    }

    public List<Document> getFileDocuments(List<FileDto> files) {
        List<Document> fileDocument = new ArrayList<>();
        for (FileDto presentFile : files) {
            fileDocument.add(generateFileDocuments(presentFile));
        }
        return fileDocument;
    }

    public Document generateFileDocuments(FileDto file) {
        return new Document(File.documentId.key, new ObjectId().toString())
                .append(File.name.key, file.getFileName())
                .append(File.extension.key, file.getFileExtension())
                .append(File.fileScore.key, gson.toJson(file.getFileScore()))
                .append(File.linesOfCodeChanges.key, gson.toJson(file.getLinesOfCodeChanges()))
                .append(File.isIgnored.key, file.isIgnored());
    }
}
