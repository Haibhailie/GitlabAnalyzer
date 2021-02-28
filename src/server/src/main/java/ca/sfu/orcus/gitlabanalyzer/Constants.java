package ca.sfu.orcus.gitlabanalyzer;

public interface Constants {
    long EPOCH_TO_DATE_FACTOR = 1000; // to multiply a long by 1000 to convert to milliseconds, for Java's date constructor
    String EARLIEST_DATE = "1973-03-30T00:00:00Z"; // earliest date commitsApi works with
    long EARLIEST_DATE_LONG = 102297600;
    long DEFAULT_UNTIL = -1;
}
