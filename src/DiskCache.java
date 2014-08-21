import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class DiskCache<K> extends Cache<K, Object>
{
    private String basePath;
    private MemCache<K, Path> memCache;

    /**
     * Creates disk cache instance. This instance uses {@link MemCache} to store file names.
     *
     * @param strategy Cache strategy
     * @param capacity Cache capacity
     * @param basePath Directory name for storing cached elements
     */
    public static <K> DiskCache<K> create( Cache.Strategy strategy, int capacity, String basePath ) throws IOException
    {
        DiskCache<K> cache = new DiskCache<>( capacity, basePath );
        cache.memCache = new MemCache<K, Path>( strategy, capacity )
        {
            @Override
            public boolean removeEldestEntryImpl( Map.Entry<K, Path> eldest, Strategy strategy )
            {
                if( getSize() > capacity ) {
                    switch( strategy ) {
                        case LRU:
                            cache.removeFile( eldest.getKey() );
                            return true;
                        case MRU:
                            K key = removeMostRecentlyUsedElement();
                            cache.removeFile( key );
                            return false;
                    }
                }
                return false;
            }
        };
        Path path = Paths.get( basePath );
        if( Files.exists( path ) ) {
            Utils.removeRecursive( path );
        }
        Files.createDirectory( path );
        return cache;
    }

    /**
     * Describes rule for key-to-filename conversion
     * TODO: Get rid of hashCode because of collision probability.
     */
    public static <K> String getFileName( K key )
    {
        return Integer.toHexString( key.hashCode() );
    }

    public DiskCache( int capacity, String basePath )
    {
        super( capacity );
        this.basePath = basePath;
    }

    public Path getPath( K key )
    {
        return Paths.get( basePath, getFileName( key ) );
    }

    @Override
    public int getSize()
    {
        return memCache.getSize();
    }

    @Override
    public Object get( K key )
    {
        Object result = null;
        Path path = memCache.get( key );
        if( path != null && Files.isReadable( path ) ) {
            try {
                byte[] buf = Files.readAllBytes( path );
                ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( buf ) );
                result = in.readObject();
            } catch( IOException | ClassCastException | ClassNotFoundException e ) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Object put( K key, Object val )
    {
        Path path = Paths.get( basePath, getFileName( key ) );
        try {
            FileOutputStream file = new FileOutputStream( path.toFile() );
            ObjectOutputStream out = new ObjectOutputStream( file );
            out.writeObject( val );
            out.close();
            memCache.put( key, path );
            return val;
        } catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean containsKey( K key )
    {
        return memCache.containsKey( key );
    }

    private void removeFile( K key )
    {
        try {
            Files.deleteIfExists( getPath( key ) );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
