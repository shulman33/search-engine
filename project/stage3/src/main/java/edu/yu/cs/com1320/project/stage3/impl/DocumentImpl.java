package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.Trie;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage3.Document;

import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document {
    private URI uri;
    private String text;
    private byte[] binaryData;
    private Set<String> wordSet = new HashSet<>();
    private Map<String,Integer> wordCount = new HashMap<>();
    private Trie<Document> trie = new TrieImpl<>();

    public DocumentImpl(URI uri, String str) {
        if((uri == null || uri.toString().isEmpty()) || (str == null || str.isEmpty())){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.text = str;
        String[] textSplit = text.split(" ");
        for (String string : textSplit){
            string = string.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
            if (!wordCount.containsKey(string)){
                wordCount.put(string,1);
            }else {
                wordCount.put(string,wordCount.get(string) + 1);
            }
            wordSet.add(string);
        }
    }

    public DocumentImpl(URI uri, byte[] byteArray) {
        if((uri == null || uri.toString().isEmpty()) || (byteArray == null || byteArray.length == 0)){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = byteArray;
    }

    @Override
    public String getDocumentTxt() {
        return this.text;
    }

    @Override
    public byte[] getDocumentBinaryData() {
        return this.binaryData;
    }

    @Override
    public URI getKey() {
        return this.uri;
    }

    @Override
    public int wordCount(String word) {
        String newWord = word.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
        if (getDocumentBinaryData() == null && wordCount.containsKey(newWord)){
            return wordCount.get(newWord);
        }
        return 0;
    }

    @Override
    public Set<String> getWords() {
        if (getDocumentBinaryData() != null){
            return new HashSet<>();
        }
        return wordSet;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0); result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

    @Override
    public boolean equals(Object other) {
        return this.hashCode() == other.hashCode();
    }

}
