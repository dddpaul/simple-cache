import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemCache<K, V> extends Cache<K, V>
{
    public MemCache( int capacity )
    {
        super( capacity );
    }

    /**
     * Creates LRU/MRU cache instance based on {@link LinkedHashMap}.
     * @see <a href="http://www.javaspecialist.ru/2012/02/java-lru-cache.html">Java LRU cache</a>
     * @param strategy  Cache strategy
     * @param capacity  Cache capacity
     */
    public static <K, V> MemCache<K, V> create( Cache.Strategy strategy, final int capacity )
    {
        MemCache<K, V> cache = new MemCache<>( capacity );
        cache.createData( strategy );
        return cache;
    }

    public void createData( Cache.Strategy strategy )
    {
        data = new LinkedHashMap<K, V>( capacity, 0.75f, true )
        {
            @Override
            protected boolean removeEldestEntry( Map.Entry<K, V> eldest )
            {
                return removeEldestEntryImpl( data, eldest, strategy );
            }
        };
    }

    @Override
    public boolean removeEldestEntryImpl( Map<K, V> data, Map.Entry<K, V> eldest, Cache.Strategy strategy )
    {
        switch( strategy ) {
            case LRU:
                break;
            case MRU:
                if( getSize() > capacity ) {
                    // Remove next to last element because it was most recently used
                    Iterator it = data.entrySet().iterator();
                    for( int i = 0; i < data.size() - 1; i++ ) {
                        it.next();
                    }
                    it.remove();
                    return false;
                }
                break;
        }
        return getSize() > capacity;
    }
}
