import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Sample usage of {@link BackedCache}
 */
public class SampleApp
{
    public static class Options
    {
        @Option( name = "-s", usage = "cache strategy" )
        public Cache.Strategy strategy = Cache.Strategy.LRU;

        @Option( name = "-c1", usage = "level-1 (memory) cache capacity" )
        public int capacity1 = 4;

        @Option( name = "-c2", usage = "level-2 (disk) cache capacity" )
        public int capacity2 = 10;

        @Option( name = "-d", usage = "level-2 cache directory (will be erased and recreated)" )
        public String basePath = "/tmp/simple-cache";

        @Argument( usage = "directory to read" )
        public String dirToRead = "resources";
    }

    public static void main( String[] args ) throws IOException
    {
        Options opt = new Options();
        CmdLineParser parser = new CmdLineParser( opt );

        try {
            parser.parseArgument( args );
            if( opt.dirToRead == null ) {
                throw new CmdLineException( parser, "No directory to read is given" );
            }
        } catch( CmdLineException e ) {
            System.err.println( e.getMessage() );
            parser.printUsage( System.err );
            System.exit( 1 );
        }

        File dirToRead = new File( opt.dirToRead );
        if( !dirToRead.isDirectory() ) {
            throw new IOException( "Can't open directory" );
        }

        File[] files = dirToRead.listFiles();
        if( files == null || files.length == 0 ) {
            throw new IOException( "Directory is empty" );
        }

        BackedCache<String> cache = BackedCache.create( opt.strategy, opt.capacity1, opt.capacity2, opt.basePath );
        System.out.println( String.format( "Cache is created: strategy = %s, capacity = %d", cache.getStrategy(), cache.getCapacity() ) );

        for( File file : files ) {
            byte[] buf = (byte[]) cache.get( file.getName() );
            if( buf == null ) {
                buf = Files.readAllBytes( file.toPath() );
                cache.put( file.getName(), buf );
            }
        }
    }
}
