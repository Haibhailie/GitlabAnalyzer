package ca.sfu.orcus.gitlabanalyzer.note;

import org.gitlab4j.api.models.Note;
import java.util.Date;

public class NoteDto {

    private int id;
    private int wordcount;
    private String content;
    private Date date;
    private String context;
    private String webUrl;

    public NoteDto(Note presentNote, String webUrl) {
        setId(presentNote.getId());
        setWordcount(countWords(presentNote.getBody()));
        setContent(presentNote.getBody());
        setDate(presentNote.getCreatedAt());
        setContext(presentNote.getNoteableType());
        setWebUrl(webUrl);
    }

    private int countWords(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }

        String[] words = str.split("\\s+");
        return words.length;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWordcount(int wordcount) {
        this.wordcount = wordcount;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
