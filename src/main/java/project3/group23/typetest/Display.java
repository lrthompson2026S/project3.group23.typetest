
package project3.group23.typetest;

import java.util.List;

import io.leego.banana.Ansi;

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

        double countDown = state.getCountDown();
        switch (state.getScreen())
        {
            case MAIN_MENU ->
                renderMenu( "Main Menu", state );

            case DIFFICULTY_MENU ->
                renderMenu( "Select Difficulty", state );

            case COUNTDOWN -> {
                console.append( "Get ready...\n" );
                console.append( String.valueOf( (int) Math.ceil( state.getCountDown() ) ) );
                console.append( "...\n" );
            }
            case GO_SCREEN -> {
                console.append( "\nGO!\n" );
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

        console.append( "\n\t" )
            .append( Ansi.ansify( state.getTypedText().toUpperCase(), Ansi.GREEN ) )
            .append( state.getCurrentWord().toUpperCase().substring( state.getTypedText().length() ) )
            .append( "\n" );

        char incorrect = state.getLastIncorrect();
        if ( incorrect != 0 )
        {
            console.append( "\t" )
                .append( " ".repeat( state.getTypedText().length() ) )
                .append( Ansi.ansify( String.valueOf( incorrect ).toUpperCase(), Ansi.RED ) )
                .append( "\n" );
        }
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
