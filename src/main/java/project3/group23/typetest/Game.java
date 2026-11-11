package project3.group23.typetest;

import io.leego.banana.Ansi;

public class Game
{
    private State state;
    private Controller controller;
    private Display display;
    private Queue<Action> actions;
    private Console console;
    private long lastGeneration;

    public Game()
    {
        actions = new Queue<>();
        console = Console.get();
    }

    public void run()
    {
        long start = System.nanoTime();
        long end;
        double dt = 0;

        boolean running = true;
        while (running)
        {
            console.append( "   ".repeat( 3 ) ).append( "FPS: " )
                .append( String.valueOf( 1 / Math.max( 1.0e-7, dt ) ) );
            console.append( "\n" );
            console.getActions( actions, 15 );
            while (!actions.isEmpty())
            {
                console.append( "Input: " );
                Action current = actions.dequeue();
                switch (current.type())
                {
                    case ENTER -> console.append( "[ ENTER ]" );
                    case ESCAPE -> {
                        console.append( "[ ESCAPE ]" );
                        running = false;
                    }
                    case ARROW_UP -> console.append( "[ ARROW UP ]" );
                    case ARROW_DOWN -> console.append( "[ ARROW DOWN ]" );
                    case CHARACTER -> console.append( "[ CHARACTER ] ('" )
                        .append( Character.toString( current.value() ) ).append( "')" );
                    default -> console.append( "[ UNKNOWN ]", Ansi.RED );
                }
                console.append( "\n" );
            }
            console.clear();
            console.display();

            end = System.nanoTime();
            dt = Math.max( 1, end - start ) * 1.0e-9;
            start = end;
        }

        console.append( "---".repeat( 8 ) ).append( "  END  " ).append( "---".repeat( 8 ) );
        console.display();

        console.close();
    }
}
