package edu.yu.cs.com1320.project;

import java.net.URI;
import java.util.Arrays;

public class Utils {
    public static int calculateHashCode(URI uri, String text, byte[] binaryData) {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

}
