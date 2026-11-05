package project3.group23.typetest;

public class State
{
    private long generation = 0;
    private boolean dirty = false;
    private boolean running = true;
    private Screen screen;
    private int selectedOption = 0;
    private Difficulty difficulty;
    private String currentWord;
    private String typedText;
    private int correctCharacters;
    private int incorrectCharacters;
    private long elapsedTime;

    public long getGeneration()
    { return 0; }
    public boolean isRunning()
    { return false; }
    public Screen getScreen()
    { return null; }
    public int getSelectedOption()
    { return 0; }
    public List<String> getMenuOptions()
    { return null; }
    public Difficulty getDifficulty()
    { return null; }
    public String getCurrentWord()
    { return null; }
    public String getTypedText()
    { return null; }
    public int getCorrectCharacters()
    { return 0; }
    public int getIncorrectCharacters()
    { return 0; }
    public long getElapsedTime()
    { return 0; }
    public void moveSelection( int delta )
    {

    }
    public void confirm()
    {

    }
    public void back()
    {

    }
    public void setDifficulty( Difficulty difficulty )
    {

    }
    public void type( char character )
    {

    }
    public void backspace()
    {

    }
    public void advanceWord()
    {

    }
    public void update( long deltaTime )
    {

    }
    public void endGeneration()
    {

    }
}
