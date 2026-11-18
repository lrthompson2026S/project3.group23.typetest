package project3.group23.typetest;

public class StateTest extends BaseTest
{
    public void testInitial()
    {
        State state = new State();

        assertTrue( state.isRunning() );
        assertEquals( Screen.MAIN_MENU, state.getScreen() );
        assertEquals( 0, state.getSelectedOption() );
        assertEquals( Difficulty.EASY, state.getDifficulty() );
        assertEquals( "apple", state.getCurrentWord() );
        assertEquals( "", state.getTypedText() );
        assertEquals( 0, state.getGeneration() );
    }

    public void testSelection()
    {
        State state = new State();

        state.moveSelection( -1 );
        assertEquals( 1, state.getSelectedOption() );

        state.moveSelection( 1 );
        assertEquals( 0, state.getSelectedOption() );
    }

    public void testStart()
    {
        State state = new State();

        state.confirm();

        assertEquals( Screen.DIFFICULTY_MENU, state.getScreen() );
        assertEquals( 0, state.getSelectedOption() );
        assertEquals( 3, state.getMenuOptions().size() );
    }

    public void testDifficulty()
    {
        State state = new State();
        state.confirm();
        state.moveSelection( 1 );
        state.confirm();

        assertTrue( state.getDifficulty() == Difficulty.MEDIUM );
        assertTrue( state.getScreen() == Screen.COUNTDOWN );
        assertTrue( state.getCountDown() == 3.0 );
        assertEquals( 0, state.getSelectedOption() );
    }

    public void testCountdown()
    {
        State state = new State();
        state.confirm();
        state.confirm();

        state.update( 3.0 );
        assertTrue( state.getScreen() == Screen.GO_SCREEN );
        assertTrue( state.getCountDown() == 0.5 );

        state.update( 0.5 );
        assertTrue( state.getScreen() == Screen.TYPING );
        assertTrue( state.getCountDown() == 15.0 );

        state.update( 15.0 );
        assertTrue( state.getScreen() == Screen.RESULTS );
        assertTrue( state.getCountDown() == 0.0 );
    }

    public void testTyping()
    {
        State state = new State();

        state.type( 'a' );
        state.type( 'x' );

        assertEquals( "a", state.getTypedText() );
        assertEquals( 1, state.getCorrectCharacters() );
        assertEquals( 1, state.getIncorrectCharacters() );
    }

    public void testCompleteWord()
    {
        State state = new State();

        state.type( 'a' );
        state.type( 'p' );
        state.type( 'p' );
        state.type( 'l' );
        state.type( 'e' );

        assertEquals( "house", state.getCurrentWord() );
        assertEquals( "", state.getTypedText() );
    }

    public void testBack()
    {
        State state = new State();
        state.confirm();
        state.confirm();

        state.back();

        assertTrue( state.getScreen() == Screen.MAIN_MENU );
        assertEquals( 0, state.getSelectedOption() );
        assertTrue( state.getCountDown() == 0.0 );
    }

    public void testGeneration()
    {
        State state = new State();

        state.endGeneration();
        assertEquals( 0, state.getGeneration() );

        state.moveSelection( 1 );
        state.endGeneration();
        assertEquals( 1, state.getGeneration() );

        state.endGeneration();
        assertEquals( 1, state.getGeneration() );
    }
}
