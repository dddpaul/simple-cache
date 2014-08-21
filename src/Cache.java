import java.util.Iterator;
import java.util.Map;

public abstract class Cache<K, V>
{
    public static enum Strategy
    {
        LRU, MRU
    }

    protected Map<K, V> data;
    protected int capacity;

    public Cache( int capacity )
    {
        this.capacity = capacity;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public int getSize()
    {
        return data.size();
    }

    public V get( K key )
    {
        return data.get( key );
    }

    public V put( K key, V val )
    {
        return data.put( key, val );
    }

    public V remove( K key )
    {
        return data.remove( key );
    }

    public boolean containsKey( K key )
    {
        return data.containsKey( key );
    }

    public boolean removeEldestEntryImpl( Map<K, V> data, Map.Entry<K, V> eldest, Cache.Strategy strategy )
    {
        return false;
    }
}
