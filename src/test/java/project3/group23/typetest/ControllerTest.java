package project3.group23.typetest;

public class ControllerTest extends BaseTest
{
    public void testInvalidMenuInput()
    {
        State state = new State();
        Controller controller = new Controller( state );
        Queue<Action> actions = new Queue<>();

        actions.enqueue( new Action( Action.Type.CHARACTER, 'x' ) );
        actions.enqueue( new Action( Action.Type.OTHER ) );

        controller.process( actions );

        assertEquals( Screen.MAIN_MENU, state.getScreen() );
        assertEquals( "", state.getTypedText() );
        assertEquals( 0, state.getIncorrectCharacters() );
        assertTrue( actions.isEmpty() );
    }

    public void testBackspaceIgnoredDuringTyping()
    {
        State state = new State();
        state.confirm();
        state.confirm();
        state.update( 3.0 );
        state.update( 0.5 );

        Controller controller = new Controller( state );
        Queue<Action> actions = new Queue<>();

        actions.enqueue( new Action( Action.Type.CHARACTER, 'a' ) );
        actions.enqueue( new Action( Action.Type.BACKSPACE ) );

        controller.process( actions );

        assertEquals( "a", state.getTypedText() );
        assertEquals( 1, state.getCorrectCharacters() );
    }

    public void testUnsupportedCharacterIgnored()
    {
        State state = new State();
        state.confirm();
        state.confirm();
        state.update( 3.0 );
        state.update( 0.5 );

        Controller controller = new Controller( state );
        Queue<Action> actions = new Queue<>();

        actions.enqueue(
            new Action( Action.Type.CHARACTER, 0x1F600 ) );

        controller.process( actions );

        assertEquals( "", state.getTypedText() );
        assertEquals( 0, state.getIncorrectCharacters() );
    }

    public void testControllerGameplay()
    {
        State state = new State();
        Controller controller = new Controller( state );
        Queue<Action> actions = new Queue<>();

        actions.enqueue( new Action( Action.Type.ENTER ) );
        controller.process( actions );

        assertEquals( Screen.DIFFICULTY_MENU, state.getScreen() );

        actions.enqueue( new Action( Action.Type.ENTER ) );
        controller.process( actions );

        assertEquals( Screen.COUNTDOWN, state.getScreen() );

        state.update( 3.0 );
        state.update( 0.5 );

        actions.enqueue( new Action( Action.Type.CHARACTER, 'x' ) );
        actions.enqueue( new Action( Action.Type.CHARACTER, 'a' ) );

        controller.process( actions );

        assertEquals( "a", state.getTypedText() );
        assertEquals( 1, state.getIncorrectCharacters() );
        assertEquals( 1, state.getCorrectCharacters() );
        assertEquals( '\0', state.getLastIncorrect() );
    }
}
