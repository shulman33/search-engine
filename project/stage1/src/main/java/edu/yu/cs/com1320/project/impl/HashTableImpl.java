package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl <Key,Value> implements HashTable<Key,Value> {

    private class Entry <Key,Value>{
        Key key;
        Value value;
        Entry next;
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

    private final int SIZE = 5;
    private Entry<?,?>[] table;

    public HashTableImpl(){
        table = new Entry[SIZE];
        for(int i = 0; i < SIZE; i++){
            table[i] = null;
        }
    }

    @Override
    public Value get(Key k) {
        int index = this.hashFunction(k);
        if(table[index] == null){
            return null;
        }
        Entry entry = table[index];
        while (entry != null && entry.getKey() != k){
            entry = entry.getNext();
        }
        if (entry == null){
            return null;
        }
        return (Value) entry.getValue();
    }

    @Override
    public Value put(Key k, Value v) {
        int index = this.hashFunction(k);
        Object oldValue = null;
        if(table[index] == null){
            table[index] = new Entry<Key,Value>(k,v);
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
