package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import jakarta.xml.bind.DatatypeConverter;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private File directory;
    public DocumentPersistenceManager(File baseDir) throws IOException {
        this.directory = baseDir;
        if (directory == null){
            directory = new File(System.getProperty("user.dir"));
        }

    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        String stringURI = uri.toString();
        Map<String, Integer> wordMap = val.getWordMap();
        GsonBuilder gson = new GsonBuilder();
        JsonObject json = new JsonObject();
        json.add("URI", gson.create().toJsonTree(stringURI));
        json.add("map", gson.create().toJsonTree(wordMap));
        if (val.getDocumentTxt() != null){
            json.add("txt", gson.create().toJsonTree(val.getDocumentTxt()));
        }else {
            json.add("binary", gson.create().toJsonTree(val.getDocumentBinaryData()));
        }

        File newFile = new File(directory,uri.getSchemeSpecificPart() + ".json");
        Files.createDirectories(Paths.get(newFile.getParent()));

        Writer writer = new FileWriter(newFile);
        writer.write(json.toString());
        writer.flush();
        writer.close();

    }

    private class DocumentDeseralizer implements JsonDeserializer<Document>{
        @Override
        public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Gson gson = new Gson();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Document doc = null;
            if (jsonObject.get("binary").getAsString() == null){
                try {
                    // prob dont keep as null
                    doc = new DocumentImpl(new URI(jsonObject.get("URI").getAsString()), jsonObject.get("txt").getAsString(), null);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }else {
                try {
                    doc = new DocumentImpl(new URI(jsonObject.get("URI").getAsString()), DatatypeConverter.parseBase64Binary(jsonObject.get("binary").getAsString()));
                } catch (URISyntaxException e){
                    throw new RuntimeException(e);
                }
            }
            return doc;
        }
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(Document.class, new DocumentDeseralizer()).setPrettyPrinting().create();
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        Document doc = null;
        File f = new File(directory,uri.getSchemeSpecificPart() + ".json");
        Reader r = new FileReader(f);
        doc = gson.fromJson(r, DocumentImpl.class);
        return doc;

    }

    @Override
    public boolean delete(URI uri) throws IOException {
        File deleteFile = new File(directory, uri.getSchemeSpecificPart() + ".json");
        return deleteFile.delete();
    }
}
