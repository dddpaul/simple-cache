import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DiskLruCache<K> extends Cache<K, Object>
{
    public static <K> String getFileName( K key )
    {
        return Integer.toHexString( key.hashCode() );
    }

    private String basePath;
    private LruCache<K, Path> cache;

    public Path getPath( K key )
    {
        return Paths.get( basePath, getFileName( key ) );
    }

    public DiskLruCache( int capacity, String path ) throws IOException
    {
        super( capacity );
        basePath = path;
        cache = LruCache.create( capacity, key -> {
            try {
                Files.deleteIfExists( getPath( key ) );
            } catch( IOException e ) {
                e.printStackTrace();
            }
        });
        Utils.removeRecursive( Paths.get( basePath ) );
        Files.createDirectory( Paths.get( basePath ) );
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
}
