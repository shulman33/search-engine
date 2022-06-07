package edu.yu.cs.com1320.project;

import java.util.NoSuchElementException;

/**
 * Beginnings of a MinHeap, for Stage 4 of project. Does not include the additional data structure or logic needed to reheapify an element after its last use time changes.
 * @param <E>
 */
public abstract class MinHeap<E extends Comparable<E>> {
    protected E[] elements;
    protected int count = 0;

    public abstract void reHeapify(E element);

    protected abstract int getArrayIndex(E element);

    protected abstract void doubleArraySize();

    protected boolean isEmpty() {
        return this.count == 0;
    }

    /**
     * is elements[i] > elements[j]?
     */
    protected boolean isGreater(int i, int j) {
        return this.elements[i].compareTo(this.elements[j]) > 0;
    }

    /**
     * swap the values stored at elements[i] and elements[j]
     */
    protected void swap(int i, int j) {
        E temp = this.elements[i];
        this.elements[i] = this.elements[j];
        this.elements[j] = temp;
    }

    /**
     * while the key at index k is less than its
     * parent's key, swap its contents with its parent’s
     */
    protected void upHeap(int k) {
        while (k > 1 && this.isGreater(k / 2, k)) {
            this.swap(k, k / 2);
            k = k / 2;
        }
    }

    /**
     * move an element down the heap until it is less than
     * both its children or is at the bottom of the heap
     */
    protected void downHeap(int k) {
        while (2 * k <= this.count) {
            //identify which of the 2 children are smaller
            int j = 2 * k;
            if (j < this.count && this.isGreater(j, j + 1)) {
                j++;
            }
            //if the current value is < the smaller child, we're done
            if (!this.isGreater(k, j)) {
                break;
            }
            //if not, swap and continue testing
            this.swap(k, j);
            k = j;
        }
    }

    public void insert(E x) {
        // double size of array if necessary
        if (this.count >= this.elements.length - 1) {
            this.doubleArraySize();
        }
        //add x to the bottom of the heap
        this.elements[++this.count] = x;
        //percolate it up to maintain heap order property
        this.upHeap(this.count);
    }

    public E remove() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        E min = this.elements[1];
        //swap root with last, decrement count
        this.swap(1, this.count--);
        //move new root down as needed
        this.downHeap(1);
        this.elements[this.count + 1] = null; //null it to prepare for GC
        return min;
    }
}