package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.stage2.Document;

import java.net.URI;
import java.util.Arrays;

public class DocumentImpl implements Document {
    private URI uri;
    private String text;
    private byte[] binaryData;

    public DocumentImpl(URI uri, String str) {
        if((uri == null || uri.toString().isEmpty()) || (str == null || str.isEmpty())){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.text = str;
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
