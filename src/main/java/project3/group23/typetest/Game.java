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
        state = new State();
        controller = new Controller(state);
        display = new Display(state);
        actions = new Queue<>();
        console = Console.get();
        lastGeneration = state.getGeneration();
    }

    public void run()
    {
        long start = System.nanoTime();
        long end;
        double dt = 0;

        display.render();
        while (state.isRunning())
        {
            console.getActions(actions, 15);
            controller.process(actions);
            state.update(dt);
            state.endGeneration();
            long currentGeneration = state.getGeneration();
            if(lastGeneration != currentGeneration)
            {
                display.render();
                lastGeneration = currentGeneration;
            }

            end = System.nanoTime();
            dt = Math.max( 1, end - start ) * 1.0e-9;
            start = end;
        }
        console.display();
        console.display();
        console.close();
    }
}
