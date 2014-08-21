import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class MemCacheTest extends CacheTest
{
    public static final int CAPACITY = 8;

    private MemCache<Integer, Object> cache;

    @Test
    public void testCapacity()
    {
        cache = MemCache.create( Cache.Strategy.LRU, CAPACITY );
        fillCache();

        putToCache( 9998, "New element" );
        putToCache( 9999, new byte[1024] );
        assertThat( cache.getSize(), is( cache.getCapacity() ));
    }

    @Test
    public void testRemove()
    {
        final int REMOVE_INDEX = 5;
        cache = MemCache.create( Cache.Strategy.LRU, CAPACITY );
        fillCache();

        assertNotNull( cache.get( REMOVE_INDEX ) );
        cache.remove( REMOVE_INDEX );
        assertNull( cache.get( REMOVE_INDEX ) );
    }

    @Test
    public void testLastRecentlyUsedRemove()
    {
        final int LRU_INDEX = 2;
        cache = MemCache.create( Cache.Strategy.LRU, CAPACITY );
        fillCache();

        // Call get() on all element except one
        for( int i = 0; i < CAPACITY; i++ ) {
            if( i != LRU_INDEX ) {
                assertThat( cache.get( i ), is( (Object) i ) );
            }
        }

        // Add new object to cache
        putToCache( 99999, new byte[1024] );

        // Last recently used element is removed from cache
        assertFalse( cache.containsKey( LRU_INDEX ) );
    }

    @Test
    public void testMostRecentlyUsedRemove()
    {
        final int MRU_INDEX = 2;
        cache = MemCache.create( Cache.Strategy.MRU, CAPACITY );
        fillCache();

        // Call get() on one element
        assertThat( cache.get( MRU_INDEX ), is( (Object) MRU_INDEX ) );

        // Add new object to cache
        putToCache( 99999, new byte[1024] );

        // Most recently used element is removed from cache
        assertFalse( cache.containsKey( MRU_INDEX ) );
    }

    @Override
    public Cache getCache()
    {
        return cache;
    }
}
