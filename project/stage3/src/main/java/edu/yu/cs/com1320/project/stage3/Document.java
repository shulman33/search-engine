package edu.yu.cs.com1320.project.stage3;

import java.net.URI;
import java.util.Set;

public interface Document
{
    /**
     * @return content of text document
     */
    String getDocumentTxt();

    /**
     * @return content of binary data document
     */
    byte[] getDocumentBinaryData();

    /**
     * @return URI which uniquely identifies this document
     */
    URI getKey();

    /**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    int wordCount(String word);

    /**
     * @return all the words that appear in the document
     */
    Set<String> getWords();
}