package project3.group23.typetest;

import java.util.NoSuchElementException;

public class Queue<T>
{
    private Node<T> front;
    private Node<T> back;
    private int size;

    public void enqueue( T value )
    {
        Node<T> node = new Node<>( value );
        if ( back == null )
            front = node;
        else
            back.next = node;
        back = node;
        size++;
    }
    public T dequeue()
    {
        T value = peek();
        front = front.next;
        size--;
        if ( size == 0 )
            back = null;
        return value;
    }
    public T peek()
    {
        if ( front == null )
            throw new NoSuchElementException( "Queue is empty" );
        return front.value;
    }
    public boolean isEmpty()
    { return size == 0; }
    public int size()
    { return size; }

    private static class Node<T>
    {
        public T value;
        public Node<T> next;

        public Node(T value)
        { this.value = value; }
    }
}
