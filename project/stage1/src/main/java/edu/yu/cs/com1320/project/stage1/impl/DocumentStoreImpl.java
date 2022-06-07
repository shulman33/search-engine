package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl hashTable;
    private Document document;

    public DocumentStoreImpl() {

        this.hashTable = new HashTableImpl();

    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {

        if(input == null){
            if(this.hashTable.get(uri) == null){
                return 0;
            }else {
                int oldHashCode = this.hashTable.get(uri).hashCode();
                this.hashTable.put(uri,null);
                return oldHashCode;
            }
        }

        if(uri == null || format == null){
            throw new IllegalArgumentException();
        }

        byte[] byteArray = input.readAllBytes();

        this.document = format == DocumentFormat.TXT ? new DocumentImpl(uri, new String(byteArray)) : new DocumentImpl(uri, byteArray);

        if(this.hashTable.get(uri) == null){
            this.hashTable.put(uri,document);
            return 0;
        }

        int oldHashCode = this.hashTable.get(uri).hashCode();
        this.hashTable.put(uri,document);
        return oldHashCode;
    }

    @Override
    public Document getDocument(URI uri) {
        return (Document)hashTable.get(uri);
    }

    @Override
    public boolean deleteDocument(URI uri) {
        if(hashTable.get(uri) != null){
            hashTable.put(uri,null);
            return true;
        }
        return false;
    }
}
