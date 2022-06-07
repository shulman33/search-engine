package edu.yu.cs.com1320.project.stage3;

import edu.yu.cs.com1320.project.stage3.impl.DocumentImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class DocumentTests {
    @Test
    public void wordCount() throws URISyntaxException, IOException {
        URI uri1 = new URI("http://amazon.com");
        String txt1 = "hello computer mouse water coat spring coAt";
        Document document = new DocumentImpl(uri1,txt1);
        assertEquals(1, document.wordCount("computer"));
        assertEquals(1,document.wordCount("Computer"));
        assertEquals(2,document.wordCount("COAT"));
    }

    @Test public void wordSet() throws URISyntaxException {
        URI uri1 = new URI("http://amazon.com");
        URI uri2 = new URI("http://google.com");
        String txt1 = "hello computer mouse water coat spring coAt";
        String txt2 = " ";
        Document document1 = new DocumentImpl(uri1,txt1);
        Document document2 = new DocumentImpl(uri2,txt2);
        assertEquals(6,document1.getWords().size());
        assertEquals(0,document2.getWords().size());
    }
}
