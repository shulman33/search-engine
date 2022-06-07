package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI,Document> hashTable;
    private Document document;
    private StackImpl<Undoable> commandStack;
    private Trie<Document> trie;


    public DocumentStoreImpl() {
        this.hashTable = new HashTableImpl();
        this.commandStack = new StackImpl();
        this.trie = new TrieImpl<>();
    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if(input == null){
            if(this.hashTable.get(uri) == null){
                Function<URI, Boolean> undo = (newUri) -> true;
                commandStack.push(new GenericCommand(uri,undo));
                return 0;
            }else {
                int oldHashCode = this.hashTable.get(uri).hashCode();
                this.deleteDocument(uri);
                return oldHashCode;
            }
        }
        if(uri == null || format == null){
            throw new IllegalArgumentException();
        }
        byte[] byteArray = input.readAllBytes();

        this.document = format == DocumentFormat.TXT ? new DocumentImpl(uri, new String(byteArray)) : new DocumentImpl(uri, byteArray);

        if(this.hashTable.get(uri) == null){
            return addingNeverPutDoc(uri,document);
        }
        return overwritingDoc(uri, document);
    }

    @Override
    public Document getDocument(URI uri) {
        return hashTable.get(uri);
    }

    @Override
    public boolean deleteDocument(URI uri) {
        if(hashTable.get(uri) != null){
            Document savedDocument = getDocument(uri);
            hashTable.put(uri,null);
            Function<URI,Boolean> undo = (newUri) ->{
                hashTable.put(newUri,savedDocument);
                for (String str : savedDocument.getWords()){
                    trie.put(str,savedDocument);
                }
                return true;
            };
            trieDelete(savedDocument);
            commandStack.push(new GenericCommand(uri,undo));
            return true;
        }
        Function<URI,Boolean> undo = (newUri) -> true;
        commandStack.push(new GenericCommand(uri,undo));
        return false;
    }

    @Override
    public void undo() throws IllegalStateException{
        if (commandStack.size() == 0){
            throw new IllegalStateException();
        }
        commandStack.pop().undo();
    }

    @Override
    public void undo(URI uri) throws IllegalStateException{
        if (commandStack.size() == 0){
            throw new IllegalStateException();
        }
        Undoable command;
        Stack<Undoable> temp = new StackImpl<>();
        while (genericOrSet(commandStack,uri).equals("none") && commandStack.size() != 0){
            command = commandStack.pop();
            temp.push(command);
        }
        String instance = genericOrSet(commandStack,uri);
        if (instance.equals("generic")){
            undo();
        }else if (instance.equals("set")){
            if(((CommandSet<URI>)commandStack.peek()).size() == 1) {
                ((CommandSet<URI>) commandStack.peek()).undoAll();
                commandStack.pop();
            }
            ((CommandSet<URI>) commandStack.peek()).undo(uri);
        }
        while (temp.size() > 0){
            command = temp.pop();
            commandStack.push(command);
        }
    }

    @Override
    public List<Document> search(String keyword) {
        String newKeyword = keyword.replace("[^A-Za-z0-9]", "").toLowerCase();
        return trie.getAllSorted(keyword, new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                if (o1.wordCount(newKeyword) < o2.wordCount(newKeyword)){
                    return 1;
                }else if (o1.wordCount(newKeyword) > o2.wordCount(newKeyword)){
                    return -1;
                }
                return 0;
            }
        });
    }

    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        keywordPrefix.replace("[^A-Za-z0-9]", "").toLowerCase();
        return trie.getAllWithPrefixSorted(keywordPrefix, new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                if (o1.wordCount(keywordPrefix) < o2.wordCount(keywordPrefix)){
                    return 1;
                }else if (o1.wordCount(keywordPrefix) > o2.wordCount(keywordPrefix)){
                    return -1;
                }
                return 0;
            }
        });
    }

    @Override
    public Set<URI> deleteAll(String keyword) {
        Set<Document> documents = trie.deleteAll(keyword);
        HashSet<URI> uriHashSet = new HashSet<>();
        CommandSet cmdSet = new CommandSet();
        return deleteAll(documents,uriHashSet,cmdSet);
    }

    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<Document> documents = trie.deleteAllWithPrefix(keywordPrefix);
        HashSet<URI> uriHashSet = new HashSet<>();
        CommandSet cmdSet = new CommandSet();
        return deleteAll(documents,uriHashSet,cmdSet);
    }

    private Set<URI> deleteAll(Set<Document> docs, Set<URI> uriSet, CommandSet cmdSet){
        for (Document doc : docs){
            uriSet.add(doc.getKey());
            Document document = hashTable.put(doc.getKey(),null);
            cmdSet.addCommand(new GenericCommand(doc.getKey(), undo ->{
                hashTable.put(doc.getKey(),document);
                putbackTrie(document);
                return true;
            }));
        }
        commandStack.push(cmdSet);
        return uriSet;
    }

    private int addingNeverPutDoc(URI uri, Document document){
        putNullBack(uri);
        for (String word : document.getWords()){
            trie.put(word,document);
        }
        this.hashTable.put(uri,document);
        return 0;
    }

    private int overwritingDoc(URI uri, Document document){
        int oldHashCode = this.hashTable.get(uri).hashCode();
        putDocBack(uri);
        Document oldDocument = getDocument(uri);
        trieDelete(oldDocument);
        putbackTrie(document);
        this.hashTable.put(uri,document);
        return oldHashCode;
    }

    private void putNullBack(URI uri){
        Function<URI,Boolean> undo = (newUri) ->{
            trieDelete(uri);
            this.hashTable.put(newUri,null);
            return true;
        };
        commandStack.push(new GenericCommand(uri,undo));
    }

    private void putDocBack(URI uri){
        Document savedDocument = getDocument(uri);
        Function<URI,Boolean> undo = (newUri) ->{
            trieDelete(uri);
            putbackTrie(savedDocument);
            hashTable.put(newUri,null);
            hashTable.put(newUri,savedDocument);
            return true;
        };
        commandStack.push(new GenericCommand(uri,undo));
    }

    private void putbackTrie(Document doc){
        for (String str : doc.getWords()){
            trie.put(str,doc);
        }
    }

    private void trieDelete(Document doc){
        for (String str : doc.getWords()){
            trie.delete(str,doc);
        }
    }

    private void trieDelete(URI uri){
        Document document = getDocument(uri);
        hashTable.put(uri,null);
        for (String str : document.getWords()){
            trie.delete(str,document);
        }
    }

    private String genericOrSet(Stack stack, URI uri){
        if (stack.peek() instanceof GenericCommand && ((GenericCommand<?>) stack.peek()).getTarget() == uri){
            return "generic";
        }
        if (stack.peek() instanceof CommandSet && ((CommandSet<URI>)stack.peek()).containsTarget(uri)){
            return "set";
        }
        return "none";
    }
}
