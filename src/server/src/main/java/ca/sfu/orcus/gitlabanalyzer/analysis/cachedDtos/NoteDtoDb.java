package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import org.gitlab4j.api.models.Note;
import org.springframework.data.annotation.Id;

import java.util.Date;

public final class NoteDtoDb {
    private int id;
    private String body;
    private int wordCount;
    private Date date;
    private String context;
    private String webUrl;

    public NoteDtoDb(Note note, String webUrl) {
        setId(note.getId());
        setBody(note.getBody());
        setWordCount(countWords(note.getBody()));
        setDate(note.getCreatedAt());
        setContext(note.getNoteableType());
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

    public void setBody(String body) {
        this.body = body;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof NoteDtoDb)) {
            return false;
        }

        NoteDtoDb n = (NoteDtoDb) o;

        return (this.id == n.id
                && this.body.equals(n.body)
                && this.wordCount == n.wordCount
                && this.date.equals(n.date)
                && this.context.equals(n.context)
                && this.webUrl.equals(n.webUrl));
    }
}
