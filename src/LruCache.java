import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LruCache<K, V> extends Cache<K, V>
{
    /**
     * Create LRU cache instance based on {@link LinkedHashMap}.
     * @see <a href="http://www.javaspecialist.ru/2012/02/java-lru-cache.html">Java LRU cache</a>
     * @param capacity  Cache capacity
     * @param action    Additional action to perform when eldest element is removed from cache
     */
    public static <K, V> LruCache<K, V> create( final int capacity, Consumer<K> action )
    {
        final LruCache<K, V> cache = new LruCache<>( capacity );
        cache.data = new LinkedHashMap<K, V>( capacity, 0.75f, true )
        {
            @Override
            protected boolean removeEldestEntry( Map.Entry<K, V> eldest )
            {
                if( size() > capacity && action != null ) {
                    action.accept( eldest.getKey() );
                }
                return size() > capacity;
            }
        };
        return cache;
    }

    public static <K, V> LruCache<K, V> create( final int capacity )
    {
        return create( capacity, null );
    }

    public LruCache( int capacity )
    {
        super( capacity );
    }
}
