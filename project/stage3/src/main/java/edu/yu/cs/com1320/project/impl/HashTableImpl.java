package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl <Key,Value> implements HashTable<Key,Value> {

    private class Entry <K,V>{
        Key key;
        Value value;
        Entry<K,V> next;
        Entry(Key k, Value v){
            if(k == null){
                throw new IllegalArgumentException();
            }
            key = k;
            value = v;
            next = null;
        }

        private Value getValue(){
            return value;
        }
        private void setValue(Value value){
            this.value = value;
        }
        private Key getKey(){
            return key;
        }
        private Entry getNext(){
            return next;
        }
        private void setNext(Entry next){
            this.next = next;
        }
    }

    private int counter = 0;
    private int size;
    private Entry<?,?>[] table;

    public HashTableImpl(){
        this.size = 5;
        table = new Entry[size];
        for(int i = 0; i < size; i++){
            table[i] = null;
        }
    }

    private void rehash(){
        if (counter > table.length * .75){
            Entry<?,?>[] oldTable = table;
            table = new Entry[2*size];
            for (int i = 0; i < size; i++){
                table[i] = null;
            }
            counter = 0;
            size *= 2;
            for (Entry head : oldTable){
                while (head != null){
                    Key key = (Key) head.key;
                    Value value = (Value) head.value;
                    this.put(key,value);
                    head = head.next;
                }
            }
        }
    }

    @Override
    public Value get(Key k) {
        int index = this.hashFunction(k);
        if(table[index] == null){
            return null;
        }
        Entry entry = table[index];
        while (entry != null && !entry.getKey().equals(k)){
            entry = entry.getNext();
        }
        try {
            return (Value) entry.getValue();
        }catch (NullPointerException e){
            return null;
        }
    }

    @Override
    public Value put(Key k, Value v) {
        int index = this.hashFunction(k);
        Object oldValue = null;
        if(table[index] == null){
            table[index] = new Entry<Key,Value>(k,v);
            counter++;
            this.rehash();
            return null;
        }else {
            Entry entry = table[index];
            while (entry.getNext() != null && entry.getKey() != k){
                entry = entry.getNext();
            }
            if (entry.getKey() == k){
                oldValue = entry.getValue();
                entry.setValue(v);
            }else {
                entry.setNext(new Entry(k,v));
            }
        }
       return (Value) oldValue;
    }

    private int hashFunction(Key key){
        return (key.hashCode() & 0x7fffffff) % this.table.length;
    }


}
