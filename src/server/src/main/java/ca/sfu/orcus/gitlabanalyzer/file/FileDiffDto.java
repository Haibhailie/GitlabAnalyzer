package ca.sfu.orcus.gitlabanalyzer.file;

public class FileDiffDto {

    public enum diffLineType {
        HEADER,
        ADDITION,
        ADDITION_SYNTAX,
        ADDITION_BLANK,
        ADDITION_SPACING,
        DELETION,
        LINE_NUMBER_SPECIFICATION
    }

    String diffLine;
    diffLineType lineType;

    public FileDiffDto(String diffLine) {
        this.diffLine = diffLine;
        setInitialDiffLineType();
    }

    public FileDiffDto(String diffLine, diffLineType lineType) {
        this.diffLine = diffLine;
        this.lineType = lineType;
    }

    public void setInitialDiffLineType() {
        if (diffLine.startsWith("diff")
                || diffLine.startsWith("---")
                || diffLine.startsWith("+++")) {
            lineType = diffLineType.HEADER;
        } else if (diffLine.startsWith("@@")) {
            lineType = diffLineType.LINE_NUMBER_SPECIFICATION;
        }
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
