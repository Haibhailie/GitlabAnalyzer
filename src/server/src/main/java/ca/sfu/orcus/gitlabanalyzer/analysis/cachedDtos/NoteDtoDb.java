package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import org.gitlab4j.api.models.Note;

public final class NoteDtoDb {
    private int id;
    private String content;
    private int wordCount;
    private long date;
    private String context;
    private String webUrl;
    private String parentAuthor;

    public NoteDtoDb(Note note, String webUrl, String parentAuthor) {
        setId(note.getId());
        setContent(note.getBody());
        setWordCount(countWords(note.getBody()));
        setDate(note.getCreatedAt().getTime());
        setContext(note.getNoteableType());
        setWebUrl(webUrl);
        setParentAuthor(parentAuthor);
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

    public void setContent(String content) {
        this.content = content;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void setParentAuthor(String parentAuthor) {
        this.parentAuthor = parentAuthor;
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
                && this.content.equals(n.content)
                && this.wordCount == n.wordCount
                && this.date == n.date
                && this.context.equals(n.context)
                && this.webUrl.equals(n.webUrl)
                && this.parentAuthor.equals(n.parentAuthor));
    }
}
