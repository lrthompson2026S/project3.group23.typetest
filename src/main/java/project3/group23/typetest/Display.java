
package project3.group23.typetest;

import java.util.List;

public class Display
{
    private final Console console;
    private final State state;

    public Display(State state)
    {
        console = Console.get();
        this.state = state;
    }

    public void render()
    {
        console.clear();

        switch (state.getScreen())
        {
            case MAIN_MENU ->
                renderMenu( "Main Menu", state );

            case DIFFICULTY_MENU ->
                renderMenu( "Select Difficulty", state );

            case COUNTDOWN -> {
                console.append( "Get ready...\n" );
                console.append( String.valueOf( Math.floor( state.getCountDown() * 10 ) / 10 + 1 ) );
                console.append( "...\n" );
            }

            case TYPING ->
                renderTyping( state );

            case RESULTS ->
                renderResults( state );
        }

        console.display();
    }

    private void renderMenu( String title, State state )
    {
        console.append( title ).append( "\n\n" );

        List<String> options = state.getMenuOptions();

        for (int i = 0; i < options.size(); i++)
        {
            console.append(
                i == state.getSelectedOption()
                    ? "> "
                    : "  " );

            console.append( options.get( i ) ).append( "\n" );
        }
    }

    private void renderTyping( State state )
    {
        double timeLeft = state.getCountDown();

        console.append( "Time left: " )
            .append( String.format( "%.2f s", timeLeft ) )
            .append( "\n\n" );

        console.append( "Target: " )
            .append( state.getCurrentWord() )
            .append( "\n" );

        console.append( "Typed:  " )
            .append( state.getTypedText() )
            .append( "\n" );
    }

    private void renderResults( State state )
    {
        console.append( "Results\n\n" );

        console.append( "Correct: " )
            .append( String.valueOf( state.getCorrectCharacters() ) )
            .append( "\n" );

        console.append( "Incorrect: " )
            .append( String.valueOf( state.getIncorrectCharacters() ) )
            .append( "\n" );

        console.append( "\nPress Enter to return." );
    }
}
