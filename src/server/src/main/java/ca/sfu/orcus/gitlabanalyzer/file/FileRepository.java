package ca.sfu.orcus.gitlabanalyzer.file;

import ca.sfu.orcus.gitlabanalyzer.utils.Diff.LOCDto;
import ca.sfu.orcus.gitlabanalyzer.utils.Diff.Scores;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
        fileId("fileId"),
        files("files"),
        fileDiffs("fileDiffs"),
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
        List<Document> fileDocument = doc.getList(File.files.key, Document.class);
        List<FileDto> files = new ArrayList<>();
        for (Document d : fileDocument) {
            files.add(getFileFromDocument(d));
        }
        return files;
    }

    private FileDto getFileFromDocument(Document doc) {
        return new FileDto(doc.getString(File.name.key))
                .setId(doc.getString(File.fileId.key))
                .setExtension(doc.getString(File.extension.key))
                .setTotalScore(gson.fromJson(doc.getString(File.fileScore.key), Scores.class))
                .setFileDiffDtos(gson.fromJson(doc.getString(File.fileDiffs.key), new TypeToken<List<FileDiffDto>>(){}.getType()))
                .setLinesOfCodeChanges(gson.fromJson(doc.getString(File.linesOfCodeChanges.key), LOCDto.class))
                .setIgnored(doc.getBoolean(File.isIgnored.key));
    }

    public List<Document> getFileDocuments(List<FileDto> files) {
        List<Document> filesDocument = new ArrayList<>();
        for (FileDto f : files) {
            filesDocument.add(generateFileDocuments(f));
        }
        return filesDocument;
    }

    public Document generateFileDocuments(FileDto file) {
        String id = new ObjectId().toString();
        return new Document(File.documentId.key, id)
                .append(File.fileId.key, id)
                .append(File.name.key, file.getFileName())
                .append(File.extension.key, file.getFileExtension())
                .append(File.fileScore.key, gson.toJson(file.getFileScore()))
                .append(File.fileDiffs.key, gson.toJson(file.getFileDiffDtos()))
                .append(File.linesOfCodeChanges.key, gson.toJson(file.getLinesOfCodeChanges()))
                .append(File.isIgnored.key, file.isIgnored());
    }
}
