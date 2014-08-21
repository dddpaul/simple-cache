import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

public class DiskCache<K> extends Cache<K, Object>
{
    /**
     * Creates disk cache instance. This instance uses {@link MemLruCache} to store file names.
     * @param strategy  Cache strategy
     * @param capacity  Cache capacity
     * @param basePath  Directory name for storing cached elements
     */
    public static <K> DiskCache<K> create( Cache.Strategy strategy, int capacity, String basePath ) throws IOException
    {
        DiskCache<K> cache = new DiskCache<>( capacity, basePath );
        cache.memCache = new MemCache<K, Path>( capacity )
        {
            @Override
            public boolean removeEldestEntryImpl( Map<K, Path> data, Map.Entry<K, Path> eldest, Strategy strategy )
            {
                switch( strategy ) {
                    case LRU:
                        if( getSize() > capacity ) {
                            try {
                                Files.deleteIfExists( cache.getPath( eldest.getKey() ) );
                            } catch( IOException e ) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case MRU:
                        if( getSize() > capacity ) {
                            // Remove next to last element because it was most recently used
                            Iterator<K> it = data.keySet().iterator();
                            K key = null;
                            for( int i = 0; i < data.size() - 1; i++ ) {
                                 key = it.next();
                            }
                            it.remove();
                            try {
                                Files.deleteIfExists( cache.getPath( key ) );
                            } catch( IOException e ) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                        break;
                }
                return getSize() > capacity;
            }
        };
        cache.memCache.createData( strategy );
        Path path = Paths.get( basePath );
        if( Files.exists( path )) {
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

    private String basePath;
    private MemCache<K, Path> memCache;

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
        if( Files.isReadable( path )) {
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
        Path path = Paths.get( basePath, getFileName( key ));
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
}
