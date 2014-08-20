import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V> extends Cache<K, V>
{
    public LruCache( int capacity )
    {
        super( capacity );
    }

    public static <K, V> LruCache<K, V> create( final int capacity )
    {
        LruCache<K, V> cache = new LruCache<>( capacity );
        cache.data = new LinkedHashMap<K, V>( capacity, 0.75f, true )
        {
            @Override
            protected boolean removeEldestEntry( Map.Entry eldest )
            {
                return size() > capacity;
            }
        };
        return cache;
    }
}
