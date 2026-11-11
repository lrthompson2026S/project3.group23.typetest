package project3.group23.typetest;

import java.util.List;
import java.util.Objects;

public class State
{
    private static final List<String> MAIN_MENU_OPTIONS = List.of( "Start", "Quit" );
    private static final List<String> DIFFICULTY_OPTIONS = List.of( "Easy", "Medium", "Hard" );
    private static final List<String> EASY_WORDS = List.of( "apple", "house", "river", "table" );
    private static final List<String> MEDIUM_WORDS = List.of( "keyboard", "computer", "practice", "language" );
    private static final List<String> HARD_WORDS = List
        .of( "polymorphism", "synchronization", "implementation", "characterization" );

    private long generation = 0;
    private boolean dirty = false;
    private boolean running = true;
    private Screen screen = Screen.MAIN_MENU;
    private int selectedOption = 0;
    private Difficulty difficulty = Difficulty.EASY;
    private String currentWord = EASY_WORDS.get( 0 );
    private String typedText = "";
    private int correctCharacters;
    private int incorrectCharacters;
    private long elapsedTime;

    public long getGeneration()
    { return generation; }
    public boolean isRunning()
    { return running; }
    public Screen getScreen()
    { return screen; }
    public int getSelectedOption()
    { return selectedOption; }
    public List<String> getMenuOptions()
    { return screen == Screen.DIFFICULTY_MENU ? DIFFICULTY_OPTIONS : MAIN_MENU_OPTIONS; }
    public Difficulty getDifficulty()
    { return difficulty; }
    public String getCurrentWord()
    { return currentWord; }
    public String getTypedText()
    { return typedText; }
    public void setTypedText( String text )
    {
        typedText = Objects.requireNonNull( text, "text" );
        markDirty();
    }
    public int getCorrectCharacters()
    { return correctCharacters; }
    public int getIncorrectCharacters()
    { return incorrectCharacters; }
    public long getElapsedTime()
    { return elapsedTime; }
    public void moveSelection( int delta )
    {
        List<String> options = getMenuOptions();
        if ( options.isEmpty() || delta == 0 )
            return;
        selectedOption = Math.floorMod( selectedOption + delta, options.size() );
        markDirty();
    }
    public void confirm()
    {
        switch (screen)
        {
            case MAIN_MENU :
                if ( selectedOption == 0 )
                {
                    screen = Screen.DIFFICULTY_MENU;
                    selectedOption = difficulty.ordinal();
                }
                else
                    running = false;
                markDirty();
                break;
            case DIFFICULTY_MENU :
                setDifficulty( Difficulty.values()[selectedOption] );
                screen = Screen.COUNTDOWN;
                selectedOption = 0;
                elapsedTime = 0;
                markDirty();
                break;
            case RESULTS :
                screen = Screen.MAIN_MENU;
                selectedOption = 0;
                markDirty();
                break;
            default :
                break;
        }
    }
    public void back()
    {
        switch (screen)
        {
            case DIFFICULTY_MENU :
                screen = Screen.MAIN_MENU;
                selectedOption = 0;
                markDirty();
                break;
            case COUNTDOWN :
            case TYPING :
            case RESULTS :
                screen = Screen.MAIN_MENU;
                selectedOption = 0;
                elapsedTime = 0;
                markDirty();
                break;
            default :
                break;
        }
    }
    public void setDifficulty( Difficulty difficulty )
    {
        this.difficulty = Objects.requireNonNull( difficulty, "difficulty" );
        markDirty();
    }
    public void type( char character )
    {
        if ( typedText.length() < currentWord.length()
            && currentWord.charAt( typedText.length() ) == character )
            correctCharacters++;
        else
            incorrectCharacters++;
        typedText += character;
        markDirty();
    }
    public void submit()
    {
        if ( typedText.equals( currentWord ) )
            advanceWord();
    }
    public void stop()
    {
        if ( running )
        {
            running = false;
            markDirty();
        }
    }
    public void backspace()
    {
        if ( typedText.isEmpty() )
            return;
        int index = typedText.length() - 1;
        if ( index < currentWord.length() && typedText.charAt( index ) == currentWord.charAt( index ) )
            correctCharacters--;
        else
            incorrectCharacters--;
        typedText = typedText.substring( 0, index );
        markDirty();
    }
    public void advanceWord()
    {
        List<String> words = wordsForDifficulty();
        int nextWord = words.indexOf( currentWord ) + 1;
        currentWord = words.get( nextWord % words.size() );
        typedText = "";
        markDirty();
    }
    public void update( long deltaTime )
    {
        if ( deltaTime < 0 )
            throw new IllegalArgumentException( "deltaTime must not be negative" );
        elapsedTime += deltaTime;
        markDirty();
    }
    public void endGeneration()
    {
        if ( dirty )
        {
            generation++;
            dirty = false;
        }
    }

    private List<String> wordsForDifficulty()
    {
        return switch (difficulty)
        {
            case EASY -> EASY_WORDS;
            case MEDIUM -> MEDIUM_WORDS;
            case HARD -> HARD_WORDS;
        };
    }

    private void markDirty()
    { dirty = true; }
}
