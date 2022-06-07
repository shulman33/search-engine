package edu.yu.cs.com1320.project.impl;

import java.util.Arrays;
import java.util.List;

/**
 * REFERENCE ONLY for stage 3. Do not extend directly.
 * @param <Value>
 */
public abstract class TooSimpleTrie<Value>
{
    private static final int alphabetSize = 256; // extended ASCII
    private Node root; // root of trie

    public static class Node<Value>
    {
        protected Value val;
        protected Node[] links = new Node[TooSimpleTrie.alphabetSize];
    }

    /**
     * Returns the value associated with the given key.
     *
     * @param key the key
     * @return the value associated with the given key if the key is in the trie and {@code null} if not
     */
    public List<Value> getAllSorted(String key)
    {
        Node x = this.get(this.root, key, 0);
        if (x == null)
        {
            return null;
        }
        return (List<Value>)Arrays.asList((Value)x.val);
    }

    /**
     * A char in java has an int value.
     * see http://docs.oracle.com/javase/8/docs/api/java/lang/Character.html#getNumericValue-char-
     * see http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.2
     */
    private Node get(Node x, String key, int d)
    {
        //link was null - return null, indicating a miss
        if (x == null)
        {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length())
        {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }

    public void put(String key, Value val)
    {
        //deleteAll the value from this key
        if (val == null)
        {
            this.deleteAll(key);
        }
        else
        {
            this.root = put(this.root, key, val, 0);
        }
    }
    /**
     *
     * @param x
     * @param key
     * @param val
     * @param d
     * @return
     */
    private Node put(Node x, String key, Value val, int d)
    {
        //create a new node
        if (x == null)
        {
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length())
        {
            x.val = val;
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    public void deleteAll(String key)
    {
        this.root = deleteAll(this.root, key, 0);
    }

    private Node deleteAll(Node x, String key, int d)
    {
        if (x == null)
        {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length())
        {
            x.val = null;
        }
        //continue down the trie to the target node
        else
        {
            char c = key.charAt(d);
            x.links[c] = this.deleteAll(x.links[c], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.val != null)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty	
        for (int c = 0; c <TooSimpleTrie.alphabetSize; c++)
        {
            if (x.links[c] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }
}