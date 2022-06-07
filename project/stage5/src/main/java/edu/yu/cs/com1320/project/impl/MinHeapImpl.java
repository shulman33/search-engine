package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

public class MinHeapImpl <T extends Comparable<T>> extends MinHeap<T>{

    public MinHeapImpl(){elements = (T[]) new Comparable[20];}

    @Override
    public void reHeapify(T element) {
        MinHeap<T> heap = new MinHeapImpl<>();
        for(T n : elements) {
            if (n != null) {
                heap.insert((T) n);
            }
        }
        for(int i = 1; i <= super.count; i++){
            elements[i] = heap.remove();
        }
    }

    @Override
    protected int getArrayIndex(T element) {
        for( int index = 1; index <= super.count; index++ ){
            if(element.equals(super.elements[index]) ){
                return index;
            }
        }
        return -1;
    }

    @Override
    protected void doubleArraySize() {
        Comparable[] temp = (T[])new Comparable[elements.length * 2];
        for (int i = 0; i < elements.length; i++){
            elements[i] = (T) temp[i];
        }
        elements = (T[]) temp;
    }
}
