import java.io.IOException;
import java.util.Map;

public class BackedCache<K> extends Cache<K, Object>
{
    private MemCache<K, Object> frontCache;
    private DiskCache<K> backingCache;

    /**
     * Creates two-level cache instance. First level is memory, second is disk.
     * @param strategy  Cache strategy
     * @param capacity1  Level-1 cache capacity
     * @param capacity2  Level-2 cache capacity
     * @param basePath  Directory name for storing cached elements
     */
    public static <K> BackedCache<K> create( Cache.Strategy strategy, int capacity1, int capacity2, String basePath ) throws IOException
    {
        BackedCache<K> cache = new BackedCache<>( capacity1 + capacity2 );
        cache.backingCache = DiskCache.create( strategy, capacity2, basePath );
        cache.frontCache = new MemCache<K, Object>( strategy, capacity1 )
        {
            @Override
            public boolean removeEldestEntryImpl( Map.Entry<K, Object> eldest, Strategy strategy )
            {
                if( getSize() > capacity ) {
                    switch( strategy ) {
                        case LRU:
                            cache.moveBackward( eldest.getKey() );
                            return true;
                        case MRU:
                            K key = removeMostRecentlyUsedElement();
                            cache.moveBackward( key );
                            return false;
                    }
                }
                return false;
            }
        };
        return cache;
    }

    public BackedCache( int capacity )
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

    private void moveBackward( K key )
    {
        backingCache.put( key, get( key ) );
    }
}
