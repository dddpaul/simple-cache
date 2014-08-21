import java.io.IOException;
import java.util.Map;

public class BackedCache<K> implements Cache<K, Object>
{
    private Strategy strategy;
    private int capacity;
    private MemCache<K, Object> frontCache;
    private DiskCache<K> backingCache;

    /**
     * Creates two-level cache instance. First level is memory, second is disk.
     * @param strategy  Cache strategy
     * @param capacity1  Level-1 cache capacity
     * @param capacity2  Level-2 cache capacity
     * @param basePath  Directory name for storing cached elements
     */
    public static <K> BackedCache<K> create( Strategy strategy, int capacity1, int capacity2, String basePath ) throws IOException
    {
        int capacity = capacity1 + capacity2;
        BackedCache<K> cache = new BackedCache<>( strategy, capacity );
        cache.backingCache = DiskCache.create( strategy, capacity2, basePath );
        cache.frontCache = new MemCache<K, Object>( strategy, capacity1 )
        {
            @Override
            public boolean removeEldestEntryImpl( Map.Entry<K, Object> eldest, Strategy strategy )
            {
                if( cache.frontCache.getSize() > capacity1 ) {
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

    public BackedCache( Strategy strategy, int capacity )
    {
        this.strategy = strategy;
        this.capacity = capacity;
    }

    @Override
    public Strategy getStrategy()
    {
        return strategy;
    }

    @Override
    public int getCapacity()
    {
        return capacity;
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
    public boolean remove( K key )
    {
        return frontCache.remove( key ) || backingCache.remove( key );
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
