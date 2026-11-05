package project3.group23.typetest;

public class Queue<T>
{
    private Node<T> front;
    private Node<T> back;
    private int size;

    public void enqueue( T value )
    {}
    public T dequeue()
    { return null; }
    public T peek()
    { return null; }
    public boolean isEmpty()
    { return false; }
    public int size()
    { return 0; }

    private static class Node<T>
    {
        public T value;
        public Node<T> next;

        public Node(T value)
        {}
    }
}
