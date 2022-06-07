package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

import static java.lang.System.nanoTime;

public class DocumentStoreImpl implements DocumentStore {
    private StackImpl<Undoable> commandStack;
    private Trie<Document> trie;
    private BTree<URI, Document> bTree;
    private int maxDocCount;
    private int maxDocBytes;
    private MinHeapImpl<Document> minHeap;
    private int currentDocCount;
    private int currentDocBytes;
    private boolean isMaxCountSet;
    private boolean isMaxBytesSet;
    private PersistenceManager persistenceManager;

    public DocumentStoreImpl() {
        try {
            this.bTree = new BTreeImpl<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.commandStack = new StackImpl();
        this.trie = new TrieImpl<>();
        this.minHeap = new MinHeapImpl();
        this.currentDocBytes = 0;
        this.currentDocCount = 0;
        this.maxDocBytes = 0;
        this.maxDocCount = 0;
        this.isMaxBytesSet = false;
        this.isMaxCountSet = false;
    }

    public DocumentStoreImpl(File baseDir) throws IOException {
        this();
        this.persistenceManager = new DocumentPersistenceManager(baseDir);
        bTree.setPersistenceManager(persistenceManager);
    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if(input == null){
            if(this.bTree.get(uri) == null){
                Function<URI, Boolean> undo = (newUri) -> true;
                commandStack.push(new GenericCommand(uri,undo));
                return 0;
            }else {
                int oldHashCode = this.bTree.get(uri).hashCode();
                this.deleteDocument(uri);
                return oldHashCode;
            }
        }
        if(uri == null || format == null){
            throw new IllegalArgumentException();
        }
        byte[] byteArray = input.readAllBytes();

        Document document = format == DocumentFormat.TXT ? new DocumentImpl(uri, new String(byteArray), null) : new DocumentImpl(uri, byteArray);
        if (!hasSpace(document)){
            try {
                deleteToMakeSpace(document);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if(this.bTree.get(uri) == null){
            return addingNeverPutDoc(uri, document,nanoTime());
        }
        return overwritingDoc(uri, document, nanoTime());
    }

    @Override
    public Document getDocument(URI uri) {
        if (uri == null){
            throw new IllegalArgumentException("uri is null");
        }

        Document doc = bTree.get(uri);
        if (doc != null){
            doc.setLastUseTime(nanoTime());
        }
        minHeap.reHeapify(doc);
        return doc;
    }
    @Override
    public boolean deleteDocument(URI uri) {
        if(bTree.get(uri) != null){
            Document savedDocument = getDocument(uri);
            bTree.put(uri,null);
            Function<URI,Boolean> undo = (newUri) ->{
                bTree.put(newUri,savedDocument);
                for (String str : savedDocument.getWords()){
                    trie.put(str,savedDocument);
                }
                addToHeap(savedDocument,nanoTime());
                return true;
            };
            trieDelete(savedDocument);
            deleteFromHeap(savedDocument);
            this.currentDocBytes -= getMemory(savedDocument);
            this.currentDocCount -= 1;
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
        List<Document> documents = this.trie.getAllSorted(keyword,(doc1, doc2) -> {
            if ( doc1.wordCount(newKeyword) < doc2.wordCount(newKeyword)) {
                return 1;
            } else if (doc1.wordCount(newKeyword) > doc2.wordCount(newKeyword)) {
                return -1;
            }
            return 0;});
        try {
            loopAndSet(documents);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return documents;
    }
    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        String newKeywordPrefix = keywordPrefix.replace("[^A-Za-z0-9]", "").toLowerCase();
        List<Document> documents = this.trie.getAllWithPrefixSorted(keywordPrefix,(doc1, doc2) -> {
            if ( doc1.wordCount(newKeywordPrefix) < doc2.wordCount(newKeywordPrefix)) {
                return 1;
            } else if (doc1.wordCount(newKeywordPrefix) > doc2.wordCount(newKeywordPrefix)) {
                return -1;
            }
            return 0;});
        try {
            loopAndSet(documents);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return documents;
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

    @Override
    public void setMaxDocumentCount(int limit) {
        this.isMaxCountSet = true;
        this.maxDocCount = limit;
    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        this.isMaxBytesSet = true;
        this.maxDocBytes = limit;
    }

    private Set<URI> deleteAll(Set<Document> docs, Set<URI> uriSet, CommandSet cmdSet){
        for (Document doc : docs){
            uriSet.add(doc.getKey());
            Document document = bTree.put(doc.getKey(),null);
            cmdSet.addCommand(new GenericCommand(doc.getKey(), undo ->{
                bTree.put(doc.getKey(),document);
                putbackTrie(document);
                return true;
            }));
        }
        commandStack.push(cmdSet);
        return uriSet;
    }
    private int addingNeverPutDoc(URI uri, Document document, long setTime){
        putNullBack(uri);
        for (String word : document.getWords()){
            trie.put(word,document);
        }
        this.bTree.put(uri,document);
        this.currentDocBytes += getMemory(document);
        this.currentDocCount++;
        document.setLastUseTime(setTime);
        minHeap.insert(document);
        return 0;
    }
    private int overwritingDoc(URI uri, Document document, long setTime){
        int oldHashCode = this.bTree.get(uri).hashCode();
        this.deleteFromHeap(this.getDocument(uri));
        this.currentDocBytes -= getMemory(this.getDocument(uri));
        this.currentDocCount -= 1;
        putDocBack(uri);
        Document oldDocument = getDocument(uri);
        trieDelete(oldDocument);
        putbackTrie(document);
        this.bTree.put(uri,document);
        this.currentDocBytes += getMemory(document);
        this.currentDocCount++;
        document.setLastUseTime(setTime);
        minHeap.insert(document);
        return oldHashCode;
    }
    private void putNullBack(URI uri){
        Function<URI,Boolean> undo = (newUri) ->{
            trieDelete(uri);
            return true;
        };
        commandStack.push(new GenericCommand(uri,undo));
    }
    private void putDocBack(URI uri){
        Document savedDocument = getDocument(uri);
        Function<URI,Boolean> undo = (newUri) ->{
            trieDelete(uri);
            putbackTrie(savedDocument);
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
    private void addToHeap(Document doc, long timeToSet){
        this.currentDocBytes += getMemory(doc);
        this.currentDocCount += 1;
        doc.setLastUseTime(timeToSet);
        minHeap.insert(doc);
    }
    private void deleteToMakeSpace(Document doc) throws Exception {
        minHeap.reHeapify(doc);
        while (!hasSpace(doc)){
            Document document = minHeap.remove();
            stamDeleteDoc(document.getKey());

        }
    }
    private void stamDeleteDoc(URI uri) throws Exception {

        this.currentDocBytes -= getMemory(getDocument(uri));
        this.currentDocCount -= 1;
        trieDelete(getDocument(uri));
        this.bTree.moveToDisk(uri);
    }
    private void deleteFromHeap(Document doc) {
        doc.setLastUseTime(Long.MIN_VALUE);
        minHeap.reHeapify(doc);
        minHeap.remove();
    }
    private boolean hasSpace(Document doc){

        if(!isMaxBytesSet && !isMaxCountSet){return true;}

        return ((this.currentDocCount + 1) <= this.maxDocCount || !isMaxCountSet) && ((this.currentDocBytes + this.getMemory(doc)) <= this.maxDocBytes || !isMaxBytesSet);
    }
    private int getMemory(Document doc){
        String memory = doc.getDocumentTxt();
        if (memory != null){
            return memory.getBytes().length;
        } else {
            return doc.getDocumentBinaryData().length;
        }
    }
    private void loopAndSet(List<Document> documents) {
        long setTime = nanoTime();
        for (Document doc : documents){
            doc.setLastUseTime(setTime);
        }
    }
}
