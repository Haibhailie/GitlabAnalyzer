package ca.sfu.orcus.gitlabanalyzer.utils;

import java.util.Base64;

public class VariableDecoderUtil {
    public static String decode(String base) {
        byte[] decodedBytes = Base64.getDecoder().decode(System.getenv(base));
        return new String(decodedBytes);
    }
}
