package project3.group23.typetest;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jline.terminal.*;
import org.jline.utils.*;

import io.leego.banana.Ansi;

public class Main
{
    public static void main(String[] args) throws Exception {
        int code = 0;
        try {
            realMain(args);
        } catch (Throwable t) {
            t.printStackTrace();
            code = 1;
        } finally {
            if (Boolean.getBoolean("pause.on.exit")) {
                System.err.println();
                System.err.println("Press Enter to exit...");
                System.in.read();
            }
        }
        System.exit(code);
    }

    public static void realMain( String[] args )
    {
        Terminal terminal;
        try
        {
            terminal = TerminalBuilder.builder()
                .system( true )
                .provider( "ffm" )
                .dumb( false )
                .build();
        }
        catch (IOException e)
        {
            System.err.println( "Failed to create terminal: " + e.getMessage() );
            return;
        }

        terminal.writer().println( "Hello and welcome!" );

        Ansi[] styles = {
                Ansi.GREEN,
                Ansi.RED,
                Ansi.BLUE,
                Ansi.YELLOW,
                Ansi.PURPLE
        };

        AttributedStringBuilder output = new AttributedStringBuilder();

        for (int i = 0; i < styles.length; i++)
        {
            output.ansiAppend(
                Ansi.ansify( "i = " + (i + 1), styles[i] ) );
            output.append( "\n" );
        }

        terminal.writer().print( output.toAnsi() );
        terminal.writer().flush();
    }
}
