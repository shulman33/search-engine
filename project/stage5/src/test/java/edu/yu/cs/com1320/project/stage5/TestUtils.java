package edu.yu.cs.com1320.project.stage5;

import edu.yu.cs.com1320.project.stage5.Document;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

public class TestUtils {

    public static void deleteTree(File base) {
        try {
            File[] files = base.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteTree(file);
                }
                else {
                    file.delete();
                }
            }
        }
        catch (Exception e) {
        }
    }

    public static File uriToFile(File baseDir, URI uri) {
        String auth = uri.getAuthority();
        String path = uri.getRawPath().replaceAll("//", File.separator) + ".json";
        return new File(baseDir, auth + File.separator + path);
    }

    public static String getContents(File baseDir, URI uri) throws IOException {
        File file = uriToFile(baseDir, uri);
        if (!file.exists()) {
            return null;
        }
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes);
    }

    public static boolean equalButNotIdentical(Document first, Document second) throws IOException {
        if(System.identityHashCode(first) == System.identityHashCode(second)){
            return false;
        }
        if(!first.getKey().equals(second.getKey())){
            return false;
        }
        if(!first.getDocumentTxt().toLowerCase().equals(second.getDocumentTxt().toLowerCase())){
            return false;
        }
        return true;
    }
}
