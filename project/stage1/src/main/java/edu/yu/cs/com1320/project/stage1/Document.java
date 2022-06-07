package edu.yu.cs.com1320.project.stage1;

import java.net.URI;

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
}