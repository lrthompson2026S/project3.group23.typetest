package project3.group23.typetest;

import org.jline.terminal.*;
import org.jline.utils.*;

public class Main
{
    public static void main( String[] args ) throws Exception
    {
        int code = 0;
        try
        {
            (new Game()).run();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            code = 1;
        }
        finally
        {
            if ( Boolean.getBoolean( "pause.on.exit" ) )
            {
                System.err.println();
                System.err.println( "Press Enter to exit..." );
                System.in.read();
            }
        }
        System.exit( code );
    }
}
