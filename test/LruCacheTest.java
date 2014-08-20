import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class LruCacheTest extends Assert
{
    @Test
    public void testLastRecentlyUsedRemove()
    {
        final int CAPACITY = 4;
        final int LRU_INDEX = 2;

        LruCache<Integer, Object> cache = LruCache.create( CAPACITY );
        for( int i = 0; i < CAPACITY; i++ ) {
            cache.put( i, i );
        }

        // Call get() on all element except one
        for( int i = 0; i < CAPACITY; i++ ) {
            if( i != LRU_INDEX ) {
                assertThat( cache.get( i ), is( (Object) i ) );
            }
        }

        // Add new object to cache
        int newKey = CAPACITY + 1;
        int newValue = CAPACITY + 1;
        cache.put( newValue, newValue );

        // Last recently used element is removed from cache
        assertFalse( cache.containsKey( LRU_INDEX ) );
        assertThat( cache.get( newKey ), is( (Object) newValue ) );
    }
}
