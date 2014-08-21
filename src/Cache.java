public interface Cache<K, V>
{
    public enum Strategy
    {
        LRU, MRU
    }

    public Strategy getStrategy();

    public int getCapacity();

    public int getSize();

    public V get( K key );

    public V put( K key, V val );

    public boolean remove( K key );

    public boolean containsKey( K key );
}
