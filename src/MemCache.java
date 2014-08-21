import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemCache<K, V> implements Cache<K, V>
{
    private Strategy strategy;
    private int capacity;
    private Map<K, V> data;

    /**
     * Creates LRU/MRU cache instance based on {@link LinkedHashMap}.
     *
     * @param strategy Cache strategy
     * @param capacity Cache capacity
     * @see <a href="http://www.javaspecialist.ru/2012/02/java-lru-cache.html">Java LRU cache</a>
     */
    public static <K, V> MemCache<K, V> create( Strategy strategy, int capacity )
    {
        return new MemCache<>( strategy, capacity );
    }

    public MemCache( Strategy strategy, int capacity )
    {
        this.strategy = strategy;
        this.capacity = capacity;
        data = new LinkedHashMap<K, V>( capacity, 1, true )
        {
            @Override
            protected boolean removeEldestEntry( Map.Entry<K, V> eldest )
            {
                return removeEldestEntryImpl( eldest, strategy );
            }
        };
    }

    /**
     * Implementation of {@link LinkedHashMap#removeEldestEntry} method.
     * LRU strategy is maintained by {@link LinkedHashMap} itself.
     * MRU strategy demands to remove most recently used element explicitly.
     *
     * @param eldest   The least recently inserted element, has no use for MRU strategy
     * @param strategy Cache strategy
     * @return <tt>true</tt> if the eldest entry should be removed from the map by {@link LinkedHashMap}
     */
    public boolean removeEldestEntryImpl( Map.Entry<K, V> eldest, Strategy strategy )
    {
        if( getSize() > capacity ) {
            switch( strategy ) {
                case LRU:
                    return true;
                case MRU:
                    removeMostRecentlyUsedElement();
                    return false;
            }
        }
        return false;
    }

    /**
     * Removes NEXT TO LAST element from cache. Should be invoked from {@link #removeEldestEntryImpl} then:
     * - the new just inserted element is the last;
     * - the next to last is the most recent used element.
     *
     * @return Key of element to remove
     */
    public K removeMostRecentlyUsedElement()
    {
        K key = null;
        Iterator<K> it = data.keySet().iterator();
        for( int i = 0; i < data.size() - 1; i++ ) {
            key = it.next();
        }
        it.remove();
        return key;
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
        return data.size();
    }

    @Override
    public V get( K key )
    {
        return data.get( key );
    }

    @Override
    public V put( K key, V val )
    {
        return data.put( key, val );
    }

    @Override
    public boolean remove( K key )
    {
        return data.remove( key ) != null;
    }

    @Override
    public boolean containsKey( K key )
    {
        return data.containsKey( key );
    }
}
