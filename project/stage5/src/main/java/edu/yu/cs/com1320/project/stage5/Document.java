package edu.yu.cs.com1320.project.stage5;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public interface Document extends Comparable<Document>
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


    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
    long getLastUseTime();
    void setLastUseTime(long timeInNanoseconds);

    /**
     * @return a copy of the word to count map so it can be serialized
     */
    Map<String,Integer> getWordMap();

    /**
     * This must set the word to count map during deserialization
     * @param wordMap
     */
    void setWordMap(Map<String,Integer> wordMap);
}