package project3.group23.typetest;

public class Controller
{
    private State state;

    public Controller(State state)
    { this.state = state; }

    public void process( Queue<Action> actions )
    {
        while (!actions.isEmpty())
        {
            Action current = actions.dequeue();
            handle( current );
        }
    }

    private void handle( Action action )
    {
        Screen screen = state.getScreen();

        
        switch (action.type())
        {
            case ENTER -> {
                if ( screen != Screen.TYPING && screen != Screen.COUNTDOWN )
                {
                    state.confirm();
                }
            }
            case ESCAPE -> {
                if ( screen == Screen.MAIN_MENU )
                {
                    state.stop();
                }
                else
                {
                    state.back();
                }
            }
            case ARROW_UP, ARROW_DOWN -> {
                if ( screen == Screen.MAIN_MENU
                    || screen == Screen.DIFFICULTY_MENU )
                {
                    state.moveSelection(
                        action.type() == Action.Type.ARROW_UP ? -1 : 1 );
                }
            }
            case CHARACTER -> {
                if ( screen == Screen.TYPING
                    && Character.isBmpCodePoint( action.value() ) )
                {
                    state.type( (char) action.value() );
                }
            }
            default -> {
            }
        }
    }

}
