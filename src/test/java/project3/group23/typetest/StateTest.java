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

    public void testIncorrectFeedbackAndRecovery()
    {
        State state = new State();

        state.type( 'x' );
        assertEquals( 'x', state.getLastIncorrect() );
        assertEquals( "", state.getTypedText() );
        assertEquals( 1, state.getIncorrectCharacters() );

        state.type( 'a' );
        assertEquals( '\0', state.getLastIncorrect() );
        assertEquals( "a", state.getTypedText() );
        assertEquals( 1, state.getCorrectCharacters() );
        assertEquals( 1, state.getIncorrectCharacters() );
    }

    public void testRoundResets()
    {
        State state = new State();
        state.confirm();
        state.confirm();
        state.update( 3.0 );
        state.update( 0.5 );

        state.type( 'a' );
        state.type( 'x' );

        state.back();
        state.confirm();
        state.confirm();

        assertEquals( "", state.getTypedText() );
        assertEquals( 0, state.getCorrectCharacters() );
        assertEquals( 0, state.getIncorrectCharacters() );
        assertEquals( '\0', state.getLastIncorrect() );
        assertEquals( "apple", state.getCurrentWord() );
        assertEquals( Screen.COUNTDOWN, state.getScreen() );
    }

    public void testNullDifficulty()
    {
        State state = new State();

        try
        {
            state.setDifficulty( null );
            fail( "Expected NullPointerException" );
        }
        catch (NullPointerException exception)
        {
            assertEquals( Difficulty.EASY, state.getDifficulty() );
        }
    }

    public void testNegativeDeltaTime()
    {
        State state = new State();

        try
        {
            state.update( -1.0 );
            fail( "Expected IllegalArgumentException" );
        }
        catch (IllegalArgumentException exception)
        {
            assertEquals(
                "deltaTime must not be negative",
                exception.getMessage() );
        }
    }

    public void testZeroSelectionDelta()
    {
        State state = new State();

        state.moveSelection( 0 );
        state.endGeneration();

        assertEquals( 0, state.getSelectedOption() );
        assertEquals( 0, state.getGeneration() );
    }

    public void testConfirmIgnoredWhileTyping()
    {
        State state = new State();
        state.confirm();
        state.confirm();
        state.update( 3.0 );
        state.update( 0.5 );

        state.confirm();

        assertEquals( Screen.TYPING, state.getScreen() );
    }

    public void testAdvanceWordWrap()
    {
        State state = new State();

        state.advanceWord();
        assertEquals( "house", state.getCurrentWord() );

        state.advanceWord();
        assertEquals( "river", state.getCurrentWord() );

        state.advanceWord();
        assertEquals( "table", state.getCurrentWord() );

        state.advanceWord();
        assertEquals( "apple", state.getCurrentWord() );
    }

    public void testBackFromGoScreen()
    {
        State state = new State();

        state.confirm();
        state.confirm();
        state.update( 3.0 );

        assertEquals( Screen.GO_SCREEN, state.getScreen() );

        state.back();

        assertEquals( Screen.MAIN_MENU, state.getScreen() );
        assertEquals( 0.0, state.getCountDown() );
    }

    public void testStopIdempotent()
    {
        State state = new State();

        state.stop();
        assertFalse( state.isRunning() );
        state.endGeneration();
        assertEquals( 1, state.getGeneration() );

        state.stop();
        state.endGeneration();
        assertFalse( state.isRunning() );
        assertEquals( 1, state.getGeneration() );
    }

    public void testDifficultyStartingWords()
    {
        State medium = new State();
        medium.setDifficulty( Difficulty.MEDIUM );
        medium.confirm();
        medium.confirm();
        assertEquals( "keyboard", medium.getCurrentWord() );
        assertEquals( Screen.COUNTDOWN, medium.getScreen() );

        State hard = new State();
        hard.setDifficulty( Difficulty.HARD );
        hard.confirm();
        hard.confirm();
        assertEquals( "polymorphism", hard.getCurrentWord() );
        assertEquals( Screen.COUNTDOWN, hard.getScreen() );
    }

    public void testResultsNavigation()
    {
        State state = new State();
        state.confirm();
        state.confirm();
        state.update( 3.0 );
        state.update( 0.5 );
        state.update( 15.0 );

        assertEquals( Screen.RESULTS, state.getScreen() );
        state.confirm();
        assertEquals( Screen.MAIN_MENU, state.getScreen() );
        assertEquals( 0, state.getSelectedOption() );
    }

    public void testInactiveScreensDoNotAdvanceTimer()
    {
        State state = new State();

        state.update( 100.0 );
        state.endGeneration();
        assertEquals( Screen.MAIN_MENU, state.getScreen() );
        assertEquals( 0.0, state.getCountDown() );
        assertEquals( 0, state.getGeneration() );

        state.confirm();
        state.confirm();
        state.update( 3.0 );
        state.update( 0.5 );
        state.update( 15.0 );
        assertEquals( Screen.RESULTS, state.getScreen() );

        state.endGeneration();
        long generation = state.getGeneration();
        state.update( 100.0 );
        state.endGeneration();

        assertEquals( 0.0, state.getCountDown() );
        assertEquals( generation, state.getGeneration() );
    }
}
