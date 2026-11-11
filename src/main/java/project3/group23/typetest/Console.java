package project3.group23.typetest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Attributes;
import org.jline.terminal.Attributes.ControlChar;
import org.jline.terminal.Attributes.LocalFlag;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.InfoCmp;

import io.leego.banana.Ansi;

import project3.group23.typetest.Action.Type;

public class Console
{
    private static Console self = null;

    private AttributedStringBuilder out;
    private final Terminal terminal;
    private final Attributes originalAttributes;
    private final BindingReader reader;
    private final KeyMap<Action> keys;

    private Console() throws IOException
    {
        out = new AttributedStringBuilder();
        terminal = TerminalBuilder.builder()
            .system( true )
            .dumb( false )
            .provider( "ffm" )
            .encoding( "UTF-8" )
            .build();

        originalAttributes = terminal.getAttributes();

        Attributes attrs = terminal.getAttributes();
        attrs.setLocalFlag( LocalFlag.ICANON, false );
        attrs.setLocalFlag( LocalFlag.ECHO, false );
        attrs.setLocalFlag( LocalFlag.ISIG, false );
        attrs.setControlChar( ControlChar.VMIN, 1 );
        attrs.setControlChar( ControlChar.VTIME, 0 );
        terminal.setAttributes( attrs );

        // Get a non-blocking reader
        reader = new BindingReader( terminal.reader() );
        keys = new KeyMap<>();

        // Normal arrow keys
        keys.bind( new Action( Type.ARROW_UP ), "\u001b[A", "\u001bOA" );
        keys.bind( new Action( Type.ARROW_DOWN ), "\u001b[B", "\u001bOB" );

        // Unicode and other input
        keys.setUnicode( new Action( Type.CHARACTER ) );
        keys.setNomatch( new Action( Type.CHARACTER ) );

        // escape key
        keys.bind( new Action( Type.ESCAPE ), KeyMap.esc() );

        // enter key
        keys.bind( new Action( Type.ENTER, 0 ), "\r", "\n" );

        // backspace key
        keys.bind( new Action( Type.BACKSPACE ), "\u007f", "\b" );
    }

    public static Console get()
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

    public void getActions(
        Queue<Action> actions,
        int timeout_ms )
    {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos( timeout_ms );

        while (System.nanoTime() < deadline)
        {
            long remaining = Math.max(
                1,
                TimeUnit.NANOSECONDS.toMillis( deadline - System.nanoTime() ) );

            // Wait for input, but only up to the remaining time
            // any character including those of escape sequences (handles arrows, esc, etc.)
            int next = reader.peekCharacter( remaining );

            if ( next < 0 )
            {
                break; // Timeout or EOF
            }

            // Decode one binding without waiting for more characters
            Action action = reader.readBinding( keys, null, false );

            if ( action == null )
            {
                continue; // Incomplete escape sequence
            }

            if ( action.type() == Type.CHARACTER )
            {
                int codePoint = reader.getLastBinding().codePointAt( 0 );
                codePoint = Character.toLowerCase( codePoint );

                // Ignore terminal control characters
                if ( Character.isISOControl( codePoint ) )
                {
                    action = new Action( Type.OTHER );
                }
                else
                {
                    action = new Action( Type.CHARACTER, codePoint );
                }
            }

            actions.enqueue( action );
        }
    }

    public Console append( String text )
    {
        out.append( text );
        return this;
    }

    public Console append( String text, Ansi... styles )
    {
        out.append( Ansi.ansify( text, styles ) );
        return this;
    }

    public void clear()
    {
        terminal.flush();
        terminal.puts( InfoCmp.Capability.clear_screen );
        terminal.flush();
    }

    public void display()
    {
        terminal.writer().print( out.toAnsi( terminal ) );
        terminal.writer().flush();
        out = new AttributedStringBuilder();
    }

    public void close()
    {
        try
        {
            terminal.setAttributes( originalAttributes );
            terminal.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException( e );
        }
    }

}
