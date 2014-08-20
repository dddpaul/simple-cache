import java.io.IOException;

public class BackedLruCache<K> extends Cache<K, Object>
{
    /**
     * Creates two-level LRU cache instance. First level is memory, second is disk.
     * @param capacity1  Level-1 cache capacity
     * @param capacity2  Level-2 cache capacity
     * @param basePath  Directory name for storing cached elements
     */
    public static <K> BackedLruCache<K> create( int capacity1, int capacity2, String basePath ) throws IOException
    {
        BackedLruCache<K> cache = new BackedLruCache<>( capacity1 + capacity2 );
        cache.backingCache = DiskLruCache.create( capacity2, basePath );
        cache.frontCache = MemLruCache.create( capacity1, key ->
                cache.backingCache.put( key, cache.frontCache.get( key ) ) );
        return cache;
    }

    private Cache<K, Object> frontCache;
    private Cache<K, Object> backingCache;

    public BackedLruCache( int capacity )
    {
        super( capacity );
    }

    @Override
    public int getSize()
    {
        return frontCache.getSize() + backingCache.getSize();
    }

    @Override
    public Object get( K key )
    {
        Object val = frontCache.get( key );
        if( val == null ) {
            val = backingCache.get( key );
            if( val != null ) {
                moveForward( key, val );
            }
        }
        return val;
    }

    @Override
    public Object put( K key, Object val )
    {
        return frontCache.put( key, val );
    }

    @Override
    public boolean containsKey( K key )
    {
        return frontCache.containsKey( key ) || backingCache.containsKey( key );
    }

    private void moveForward( K key, Object val )
    {
        frontCache.put( key, val );
        backingCache.remove( key );
    }
}
