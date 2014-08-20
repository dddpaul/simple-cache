import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class MemLruCacheTest extends CacheTest
{
    public static final int CAPACITY = 4;

    @Before
    public void setUp()
    {
        cache = MemLruCache.create( CAPACITY );
    }

    @Test
    public void testLastRecentlyUsedRemove()
    {
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
}
