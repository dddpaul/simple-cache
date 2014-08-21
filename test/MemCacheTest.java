import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class MemCacheTest extends CacheTest
{
    public static final int CAPACITY = 4;

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
    public void testLastRecentlyUsedRemove()
    {
        cache = MemCache.create( Cache.Strategy.LRU, CAPACITY );
        final int LRU_INDEX = 2;

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
        cache = MemCache.create( Cache.Strategy.MRU, CAPACITY );
        final int MRU_INDEX = 2;

        fillCache();

        // Call get() on one element
        assertThat( cache.get( MRU_INDEX ), is( (Object) MRU_INDEX ) );

        // Add new object to cache
        putToCache( 99999, new byte[1024] );

        // Most recently used element is removed from cache
        assertFalse( cache.containsKey( MRU_INDEX ) );
    }
}
