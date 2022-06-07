package edu.yu.cs.com1320.project.stage2;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashTableTests {
    @Test
    public void rehash(){
        HashTable<Integer,Integer> hashTable = new HashTableImpl();
        for (int key = 0; key < 100000; key++){
            hashTable.put(key,key+1);
            assertEquals(hashTable.get(key),key+1);
        }
    }
    @Test
    public void rehashString(){
        HashTable<String,String> hashTable = new HashTableImpl();
        hashTable.put("Key1", "Value1");
        hashTable.put("Key2", "Value2");
        hashTable.put("Key3", "Value3");
        hashTable.put("Key4", "Value4");
        hashTable.put("Key5", "Value5");
        hashTable.put("Key6", "Value6");
        hashTable.put("Key7", "Value7");
        assertEquals("Value1", hashTable.get("Key1"));
        assertEquals("Value2", hashTable.get("Key2"));
        assertEquals("Value3", hashTable.get("Key3"));
        assertEquals("Value4", hashTable.get("Key4"));
        assertEquals("Value5", hashTable.get("Key5"));
        assertEquals("Value6", hashTable.get("Key6"));
        assertEquals("Value7", hashTable.get("Key7"));
    }


}
