import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;

public class SimpleCache
{
    public static class Options
    {
        @Option( name = "-s", usage = "cache strategy" )
        public Cache.Strategy strategy = Cache.Strategy.LRU;

        @Option( name = "-c1", usage = "level-1 (memory) cache capacity" )
        public int capacity1 = 100;

        @Option( name = "-c2", usage = "level-2 (disk) cache capacity" )
        public int capacity2 = 1000;

        @Option( name = "-d", usage = "level-2 cache directory (will be erased and recreated)" )
        public String basePath = "/tmp/simple-cache";
    }

    public static void main( String[] args ) throws IOException
    {
        Options opt = new Options();
        CmdLineParser parser = new CmdLineParser( opt );

        try {
            parser.parseArgument( args );
        } catch( CmdLineException e ) {
            System.err.println( e.getMessage() );
            parser.printUsage( System.err );
        }

        Cache<Integer, Object> cache = BackedCache.create( opt.strategy, opt.capacity1, opt.capacity2, opt.basePath );
        System.out.println( String.format( "Cache is created: strategy = %s, capacity = %d", cache.getStrategy(), cache.getCapacity() ) );
    }
}
