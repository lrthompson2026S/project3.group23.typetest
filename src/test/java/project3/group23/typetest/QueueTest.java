package project3.group23.typetest;

import java.util.NoSuchElementException;

public class QueueTest extends BaseTest
{
    public void testEmpty()
    {
        Queue<String> queue = new Queue<>();

        assertTrue( queue.isEmpty() );
        assertEquals( 0, queue.size() );
    }

    public void testEnqueue()
    {
        Queue<String> queue = new Queue<>();

        queue.enqueue( "first" );
        queue.enqueue( "second" );

        assertFalse( queue.isEmpty() );
        assertEquals( 2, queue.size() );
        assertEquals( "first", queue.peek() );
    }

    public void testFifo()
    {
        Queue<Integer> queue = new Queue<>();
        queue.enqueue( 1 );
        queue.enqueue( 2 );
        queue.enqueue( 3 );

        assertTrue( queue.dequeue() == 1 );
        assertTrue( queue.dequeue() == 2 );
        assertTrue( queue.dequeue() == 3 );
        assertTrue( queue.isEmpty() );
        assertEquals( 0, queue.size() );
    }

    public void testReuse()
    {
        Queue<String> queue = new Queue<>();
        queue.enqueue( "first" );
        assertEquals( "first", queue.dequeue() );

        queue.enqueue( "second" );

        assertEquals( "second", queue.peek() );
        assertEquals( 1, queue.size() );
        assertEquals( "second", queue.dequeue() );
        assertTrue( queue.isEmpty() );
    }

    public void testNull()
    {
        Queue<String> queue = new Queue<>();

        queue.enqueue( null );

        assertTrue( queue.peek() == null );
        assertTrue( queue.dequeue() == null );
        assertTrue( queue.isEmpty() );
    }

    public void testPeekEmpty()
    {
        Queue<String> queue = new Queue<>();

        try
        {
            queue.peek();
            fail( "peek should throw when the queue is empty" );
        }
        catch (NoSuchElementException exception)
        {
            assertEquals( "Queue is empty", exception.getMessage() );
        }
    }

    public void testDequeueEmpty()
    {
        Queue<String> queue = new Queue<>();

        try
        {
            queue.dequeue();
            fail( "dequeue should throw when the queue is empty" );
        }
        catch (NoSuchElementException exception)
        {
            assertEquals( "Queue is empty", exception.getMessage() );
        }
    }
}
