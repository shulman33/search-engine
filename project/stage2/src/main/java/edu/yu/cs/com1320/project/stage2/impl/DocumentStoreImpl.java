package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI,Document> hashTable;
    private Document document;
    private StackImpl<Command> commandStack;


    public DocumentStoreImpl() {
        this.hashTable = new HashTableImpl();
        this.commandStack = new StackImpl();

    }

    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if(input == null){
            if(this.hashTable.get(uri) == null){
                Function<URI, Boolean> undo = (newUri) -> true;
                commandStack.push(new Command(uri,undo));
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
            putNullBack(uri);
            this.hashTable.put(uri,document);
            return 0;
        }
        int oldHashCode = this.hashTable.get(uri).hashCode();
        putDocBack(uri);
        this.hashTable.put(uri,document);
        return oldHashCode;
    }

    private void putNullBack(URI uri){
        Function<URI,Boolean> undo = (newUri) ->{
            this.hashTable.put(newUri,null);
            return true;
        };
        commandStack.push(new Command(uri,undo));
    }

    private void putDocBack(URI uri){
        Document savedDocument = getDocument(uri);
        Function<URI,Boolean> undo = (newUri) ->{
            hashTable.put(newUri,null);
            hashTable.put(newUri,savedDocument);
            return true;
        };
        commandStack.push(new Command(uri,undo));
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
                return true;
            };
            commandStack.push(new Command(uri,undo));
            return true;
        }
        Function<URI,Boolean> undo = (newUri) -> true;
        commandStack.push(new Command(uri,undo));
        return false;
    }

    @Override
    public void undo() throws IllegalStateException{
        if (commandStack.size() == 0){
            throw new IllegalStateException();
        }
        Command command = commandStack.peek();
        command.undo();
        commandStack.pop();

    }

    @Override
    public void undo(URI uri) throws IllegalStateException{
        Stack<Command> temp = new StackImpl<>();
        Command command = this.commandStack.peek();
        Boolean flag = false;
        while (command != null && !command.getUri().equals(uri)){
            command = commandStack.pop();
            temp.push(command);
            command = this.commandStack.peek();
        }
        if (command == null){
            flag = true;
        }
        if (command != null){
            commandStack.pop().undo();
        }
        while (temp.size() != 0){
            command = temp.pop();
            commandStack.push(command);
        }

        if (flag){
            throw new IllegalStateException();
        }
    }
}
