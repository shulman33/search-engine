package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    private class DynamicArray<T>{
        private T[] array;

        public DynamicArray(){
            this.array = (T[]) new Object[10];
        }

        private void insert(T element, int index){
            if (index > array.length-1){
                T[] newArray = (T[]) new Object[index+1];
                for (int i = 0; i < array.length; i++){
                    newArray[i] = array[i];
                }
                array = newArray;
            }
            array[index] = element;
        }

        private T getElement(int index){
            return array[index];
        }
    }

    private DynamicArray arr;
    private int top;

    public StackImpl(){
        this.arr = new DynamicArray();
        this.top = -1;
    }

    @Override
    public void push(T element) {
        top++;
        arr.insert(element,top);
    }

    @Override
    public T pop() {
        if (top == -1){
            return null;
        }
        T item = (T) arr.getElement(top);
        arr.insert(null,top);
        top--;
        return item;
    }

    @Override
    public T peek() {
        if (top == -1){
            return null;
        }
        return (T) arr.getElement(top);

    }

    @Override
    public int size() {
        return top + 1;
    }

}
