package ca.sfu.orcus.gitlabanalyzer.utils;

import ca.sfu.orcus.gitlabanalyzer.Constants;

import java.util.Date;

public final class DateUtils {
    private static final long SECONDS_TO_MS = 1000;
    private static final long EARLIEST_DATE_EPOCH_S = 1104537600; // January 1, 2005, 12:00:00 AM GMT
    private static final Date EARLIEST_DATE = new Date(EARLIEST_DATE_EPOCH_S * SECONDS_TO_MS);
    private static final long DEFAULT_UNTIL = Long.parseLong(Constants.DEFAULT_UNTIL);

    private DateUtils() {
        throw new AssertionError();
    }

    public static Date getDateSinceOrEarliest(long since) {
        if (since <= EARLIEST_DATE_EPOCH_S) {
            return EARLIEST_DATE;
        } else {
            return new Date(since * SECONDS_TO_MS);
        }
    }

    public static Date getDateUntilOrNow(long until) {
        if (until == DEFAULT_UNTIL) {
            return new Date();
        } else if (until <= EARLIEST_DATE_EPOCH_S) {
            return EARLIEST_DATE;
        } else {
            return new Date(until * SECONDS_TO_MS);
        }
    }
}