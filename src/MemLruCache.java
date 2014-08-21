import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@Deprecated
public class MemLruCache<K, V> extends Cache<K, V>
{
    /**
     * Creates LRU cache instance based on {@link LinkedHashMap}.
     *
     * @param capacity Cache capacity
     * @param action   Additional action to perform when eldest element is removed from cache
     * @see <a href="http://www.javaspecialist.ru/2012/02/java-lru-cache.html">Java LRU cache</a>
     */
    public static <K, V> MemLruCache<K, V> create( final int capacity, Consumer<K> action )
    {
        MemLruCache<K, V> cache = new MemLruCache<>( capacity );
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

    public static <K, V> MemLruCache<K, V> create( final int capacity )
    {
        return create( capacity, null );
    }

    public MemLruCache( int capacity )
    {
        super( capacity );
    }
}
