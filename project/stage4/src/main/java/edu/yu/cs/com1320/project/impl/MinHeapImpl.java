package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

public class MinHeapImpl <E extends Comparable<E>> extends MinHeap<E>{

    public MinHeapImpl(){elements = (E[]) new Comparable[20];}

    @Override
    public void reHeapify(E element) {
        MinHeap<E> heap = new MinHeapImpl<>();
        for(E n : elements) {
            if (n != null) {
                heap.insert((E) n);
            }
        }
        for(int i = 1; i <= super.count; i++){
            elements[i] = heap.remove();
        }
    }

    @Override
    protected int getArrayIndex(E element) {
        for( int index = 1; index <= super.count; index++ ){
            if(element.equals(super.elements[index]) ){
                return index;
            }
        }
        return -1;
    }

    @Override
    protected void doubleArraySize() {
        E[] temp = (E[]) new Comparable[elements.length * 2];
        for (int i = 0; i < elements.length; i++){
            elements[i] = (E) temp[i];
        }
        elements = (E[]) temp;
    }
}
