package ca.sfu.orcus.gitlabanalyzer.file;

public class FileDiffDto {

    public enum DiffLineType {
        HEADER,
        ADDITION,
        ADDITION_SYNTAX,
        ADDITION_BLANK,
        ADDITION_SPACING,
        DELETION,
        LINE_NUMBER_SPECIFICATION
    }

    String diffLine;
    DiffLineType lineType;

    public FileDiffDto(String diffLine) {
        this.diffLine = diffLine;

    }

    public FileDiffDto(String diffLine, DiffLineType lineType) {
        this.diffLine = diffLine;
        this.lineType = lineType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof FileDiffDto)) {
            return false;
        }

        FileDiffDto m = (FileDiffDto) o;

        return (this.diffLine.equals(m.diffLine)
                && this.lineType == m.lineType);
    }
}
