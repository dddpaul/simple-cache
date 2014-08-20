import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DiskLruCache<K> extends Cache<K, Object>
{
    /**
     * Creates disk LRU cache instance. This instance uses {@link MemLruCache} to store file names.
     * @param capacity  Cache capacity
     * @param basePath  Directory name for storing cached elements
     */
    public static <K> DiskLruCache<K> create( int capacity, String basePath ) throws IOException
    {
        DiskLruCache<K> cache = new DiskLruCache<>( capacity, basePath );
        cache.memCache = MemLruCache.create( capacity, key -> {
            try {
                Files.deleteIfExists( cache.getPath( key ) );
            } catch( IOException e ) {
                e.printStackTrace();
            }
        } );
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
    private MemLruCache<K, Path> memCache;

    public DiskLruCache( int capacity, String basePath )
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
