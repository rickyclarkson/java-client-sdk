package uk.org.netvu.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class IO
{
    public static void moveFile( File oldName, File newName ) throws IOException
    {
        if ( oldName.renameTo( newName ) )
        {
            return;
        }

        if ( oldName.isDirectory() )
        {
            if ( newName.mkdirs() )
            {
                for ( File file: oldName.listFiles() )
                {
                    moveFile( file, new File( newName, file.getName() ) );
                }
                return;
            }

            throw new IOException( "Could not create the directory " + newName );
        }

        if ( newName.exists() )
        {
            throw new IOException( newName + " already exists." );
        }

        BufferedInputStream in = new BufferedInputStream( new FileInputStream( oldName ) );
        try
        {
            BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( newName ) );
            try
            {
                int x;
                while ((x = in.read()) != -1)
                {
                    out.write( x );
                }
            }
            finally
            {
                try
                {
                    out.close();
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
        finally
        {
            try
            {
                in.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }
}