import java.util.Map;

public abstract class Cache<K, V>
{
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

    public V get( K key )
    {
        return data.get( key );
    }

    public V put( K key, V val )
    {
        return data.put( key, val );
    }

    public boolean containsKey( K key )
    {
        return data.containsKey( key );
    }
}
