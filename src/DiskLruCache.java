import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiskLruCache<K> extends Cache<K, Object>
{
    private String basePath;
    private LruCache<K, Path> cache;

    public DiskLruCache( int capacity, String path ) throws IOException
    {
        super( capacity );
        basePath = path;
        cache = createLruCache( capacity, path );
        Utils.removeRecursive( Paths.get( basePath ) );
        Files.createDirectory( Paths.get( basePath ) );
    }

    public static <K, V> LruCache<K, V> createLruCache( final int capacity, final String basePath )
    {
        final LruCache<K, V> cache = new LruCache<>( capacity );
        cache.data = new LinkedHashMap<K, V>( capacity, 0.75f, true )
        {
            @Override
            protected boolean removeEldestEntry( Map.Entry eldest )
            {
                if( size() > capacity ) {
                    Path path = Paths.get( basePath, getFileName( eldest.getKey() ) );
                    try {
                        Files.deleteIfExists( path );
                    } catch( IOException e ) {
                        e.printStackTrace();
                    }
                }
                return size() > capacity;
            }
        };
        return cache;
    }

    @Override
    public Object get( K key )
    {
        Object result = null;
        Path path = cache.get( key );
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
            cache.put( key, path );
            return val;
        } catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean containsKey( K key )
    {
        return cache.containsKey( key );
    }

    public static <K> String getFileName( K key )
    {
        return Integer.toHexString( key.hashCode() );
    }
}
