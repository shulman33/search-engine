package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {
    private final int R = 256;
    private Node root;

    private class Node<Value>{
        private Node<?>[] links = new Node[R];
        private List<Value> valueList = new ArrayList<>();
    }

    public TrieImpl(){}

    @Override
    public void put(String key, Value val) {
        if (key == null){
            deleteAll(key);
        }
        root = put(root,key,val,0);
    }

    private Node put(Node x, String key, Value val, int d){
        key = key.toLowerCase();
        if (x == null){
            x = new Node();
        }
        if (d == key.length()){
            if (x.valueList.contains(val)){
                return x;
            }else {
                x.valueList.add(val);
                return x;
            }
        }
        char c = key.charAt(d);
        x.links[c] = put(x.links[c], key, val, d+1);
        return x;

    }

    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        Node x = get(this.root, key, 0);
        if (x == null){
            return new ArrayList<>();
        }
        Collections.sort(x.valueList,comparator);
        return x.valueList;
    }

    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        prefix = prefix.toLowerCase();
        List<String> prefixList = new ArrayList<>();
        List<Value> newPrefixList = new ArrayList<>();
        collect(get(root, prefix, 0), prefix, prefixList);
        for (String word : prefixList){
            if (!newPrefixList.contains(word)){
                newPrefixList.addAll(this.getAllSorted(word, comparator));
            }
        }
        Collections.sort(newPrefixList, comparator);
        return newPrefixList;
    }

    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if (prefix.isEmpty()){
            return new HashSet<>();
        }
        prefix.toLowerCase();
        List<String> prefixList = new ArrayList<>();
        Set<Value> prefixSet = new HashSet<>();
        collect(get(root, prefix, 0), prefix, prefixList);
        for (String str : prefixList){
            prefixSet.addAll(deleteAll(str));
        }
        return prefixSet;
    }

    @Override
    public Set<Value> deleteAll(String key) {
        key.toLowerCase();
        Set<Value> valueSet = new HashSet<>();
        Node node = get(this.root, key, 0);
        if (node == null){
            return valueSet;
        }
        Set<Value> copy = new HashSet<>(node.valueList);
        for (Value value : copy){
            Value val = delete(key,value);
            valueSet.add(val);
        }
        return valueSet;
    }
    
    @Override
    public Value delete(String key, Value val) {
        key = key.toLowerCase();
        Value end = null;
        List<Value> nodeValList = this.get(root,key,0).valueList;
        for (Value value : nodeValList){
            if (val.equals(value)){
                end = value;
            }
        }
        Node deletedValue = delete(root,key,val,0);
        root = deletedValue;
        if (deletedValue != null){
            return end;
        }
        return null;
    }

    private Node delete(Node x, String key, Value val ,int d){
        key = key.toLowerCase();
        if (x == null){
            return null;
        }
        if (d == key.length()){
            x.valueList.remove(val);
        }else{
            char c = key.charAt(d);
            x.links[c] = delete(x.links[c],key,val,d+1);
        }
        if (!x.valueList.isEmpty()){
            return x;
        }
        for (char c = 0; c < R; c++){
            if (x.links[c] != null){
                return x;
            }
        }
        return null;
    }

    private Node get(Node x, String key, int d){
        key = key.toLowerCase();
        if (x == null){
            return null;
        }
        if (d == key.length()){
            return x;
        }
        char c = key.charAt(d);
        return get(x.links[c], key, d+1);
    }

    private void collect(Node x, String prefix, List<String> prefixList){
        prefix = prefix.toLowerCase();
        if (x == null){
            return;
        }

        if (!x.valueList.contains(prefix)){
            prefixList.add(prefix);
        }
        for (char c = 0; c < R; c++){
            collect(x.links[c], prefix + c, prefixList);
        }

    }

}
