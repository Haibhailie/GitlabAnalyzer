package ca.sfu.orcus.gitlabanalyzer.utils;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import org.gitlab4j.api.utils.ISO8601;

import java.text.ParseException;
import java.util.Date;

public final class DateUtils {
    private static final long EPOCH_TO_DATE_FACTOR = 1000;
    private static final String EARLIEST_DATE_STRING = "2005-01-01T00:00:00Z"; // Git launched in 2015

    private DateUtils() {
        throw new AssertionError();
    }

    public static Date getDateSinceOrEarliest(long since) throws ParseException {
        Date earliestDate = ISO8601.toDate(EARLIEST_DATE_STRING);
        if (since <= earliestDate.getTime()) {
            return earliestDate;
        } else {
            return new Date(since * EPOCH_TO_DATE_FACTOR);
        }
    }

    public static Date getDateUntilOrNow(long until) throws ParseException {
        Date earliestDate = ISO8601.toDate(EARLIEST_DATE_STRING);
        long defaultUntil = Long.parseLong(Constants.DEFAULT_UNTIL);
        if (until == defaultUntil) {
            return new Date();
        } else if (until <= earliestDate.getTime()) {
            return earliestDate;
        } else {
            return new Date(until * EPOCH_TO_DATE_FACTOR);
        }
    }
}