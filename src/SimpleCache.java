import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class SimpleCache
{
    public static class Options
    {
        @Option( name = "-c", usage = "cache capacity" )
        private int capacity = 100;
    }

    public static void main( String[] args )
    {
        Options options = new Options();
        CmdLineParser parser = new CmdLineParser( options );

        try {
            parser.parseArgument( args );
        } catch( CmdLineException e ) {
            System.err.println( e.getMessage() );
            parser.printUsage( System.err );
        }

        LruCache<Integer, Object> cache = LruCache.create( options.capacity );
        System.out.println( String.format( "Cache is created: capacity = %d", cache.getCapacity() ) );
    }
}
