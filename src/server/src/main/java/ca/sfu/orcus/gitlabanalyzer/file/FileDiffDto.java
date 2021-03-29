package ca.sfu.orcus.gitlabanalyzer.file;

public class FileDiffDto {

    enum diffLineType {
        HEADER,
        ADDITION,
        DELETION,
        UNCHANGED,
        LINE_NUMBER_SPECIFICATION
    }

    String diffLine;
    diffLineType lineType;

    public FileDiffDto(String diffLine) {
        this.diffLine = diffLine;
        setDiffLineType();
    }

    public void setDiffLineType() {
        if (diffLine.startsWith("diff")
                || diffLine.startsWith("---")
                || diffLine.startsWith("+++")) {
            lineType = diffLineType.HEADER;
        } else if (diffLine.startsWith("@@")) {
            lineType = diffLineType.LINE_NUMBER_SPECIFICATION;
        } else if (diffLine.startsWith("+")) {
            lineType = diffLineType.ADDITION;
        } else if (diffLine.startsWith("-")) {
            lineType = diffLineType.DELETION;
        } else {
            lineType = diffLineType.UNCHANGED;
        }
    }

}
