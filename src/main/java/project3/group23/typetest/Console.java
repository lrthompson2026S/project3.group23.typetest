package project3.group23.typetest;

import java.io.IOException;

import io.leego.banana.Ansi;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;

public class Console
{
    private static Console self = null;

    private final AttributedStringBuilder out;
    private final Terminal terminal;

    private Console() throws IOException
    {
        out = new AttributedStringBuilder();
        terminal = TerminalBuilder.builder()
            .system( true )
            .dumb( false )
            .provider( "ffm" )
            .build();
    }

    private Console get()
    {
        if ( self == null )
        {
            try
            {
                self = new Console();
            }
            catch (IOException e)
            {
                throw new RuntimeException( e );
            }
        }

        return self;
    }
    
    public static void initialize()
    {}

    public static void getActions(Queue<Action> actions, int timeout)
    {}

    public static void append(String text)
    {}

    public static void append(String text, Ansi... styles)
    {}

    public static void clearScreen()
    {}

    public static void display()
    {}

    public static void close()
    {}

}
