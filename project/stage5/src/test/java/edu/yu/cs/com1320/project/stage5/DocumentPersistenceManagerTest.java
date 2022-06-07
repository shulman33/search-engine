package edu.yu.cs.com1320.project.stage5;


import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentPersistenceManagerTest {

    private File baseDir;
    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;
    private Document doc1;
    //variables to hold possible values for doc2
    private URI uri2;
    private String txt2;
    private Document doc2;
    //variables to hold possible values for doc2
    private URI uri3;
    private String txt3;
    private Document doc3;

    @BeforeEach
    public void init()throws Exception{
        this.baseDir = Files.createTempDirectory("stage5").toFile();
        //init values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "This is the text of doc1 in plain text No fancy file format just plain old String Computer Headphones.";
        this.doc1 = new DocumentImpl(this.uri1,this.txt1,null);
        //init values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text for doc2 A plain old String";
        this.doc2 = new DocumentImpl(this.uri2,this.txt2, null);
        //init values for doc3
        this.uri3 = new URI("http://cs.nyu.edu/datastructs/project/doc2");
        this.txt3 = "Text for NYU doc2 A plain old String";
        this.doc3 = new DocumentImpl(this.uri3,this.txt3, null);
    }

    @AfterEach
    public void cleanUp(){
        TestUtils.deleteTree(this.baseDir);
        this.baseDir.delete();
    }

    @Test
    public void stage5TestSerializationPath()throws Exception{
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(this.baseDir);
        dpm.serialize(this.uri1,this.doc1);
        assertTrue(TestUtils.uriToFile(this.baseDir,this.uri1).exists(),"file was not created where expected");
        dpm.serialize(this.uri2,this.doc2);
        assertTrue(TestUtils.uriToFile(this.baseDir,this.uri2).exists(),"file was not created where expected");
        dpm.serialize(this.uri3,this.doc3);
        assertTrue(TestUtils.uriToFile(this.baseDir,this.uri3).exists(),"file was not created where expected");
    }

    @Test
    public void stage5TestSerializationContent()throws Exception{
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(this.baseDir);
        dpm.serialize(this.uri1,this.doc1);
        String contents = TestUtils.getContents(this.baseDir,this.uri1).toLowerCase();
        assertTrue(contents.lastIndexOf(this.txt1.toLowerCase())>=0,"doc1 text contents not found in serialized file");

        dpm.serialize(this.uri2,this.doc2);
        contents = TestUtils.getContents(this.baseDir,this.uri2).toLowerCase();
        assertTrue(contents.lastIndexOf(this.txt2.toLowerCase())>=0,"doc2 text contents not found in serialized file");

        dpm.serialize(this.uri3,this.doc3);
        contents = TestUtils.getContents(this.baseDir,this.uri3).toLowerCase();
        assertTrue(contents.lastIndexOf(this.txt3.toLowerCase())>=0,"doc3 text contents not found in serialized file");
    }

    @Test
    public void stage5TestDeserialization()throws Exception{
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(this.baseDir);
        //serialize all 3 documents
        dpm.serialize(this.uri1,this.doc1);
        dpm.serialize(this.uri2,this.doc2);
        dpm.serialize(this.uri3,this.doc3);
        TestUtils.equalButNotIdentical(this.doc1,dpm.deserialize(this.uri1));
        TestUtils.equalButNotIdentical(this.doc2,dpm.deserialize(this.uri2));
        TestUtils.equalButNotIdentical(this.doc3,dpm.deserialize(this.uri3));
    }
}

