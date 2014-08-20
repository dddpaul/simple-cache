import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

public class BackedLruCacheTest extends Assert
{
    @Test
    public void testLastRecentlyUsedRemove() throws IOException
    {
        final int CAPACITY1 = 4;
        final int CAPACITY2 = 8;
        final int LRU_INDEX = 2;

        BackedLruCache<Integer> cache = BackedLruCache.create( CAPACITY1, CAPACITY2, DiskLruCacheTest.BASE_PATH );
        assertThat( cache.getCapacity(), is( CAPACITY1 + CAPACITY2 ) );

        // Fill level-1 cache
        for( int i = 0; i < CAPACITY1; i++ ) {
            cache.put( i, i );
        }

        // Call get() on all element except one
        for( int i = 0; i < CAPACITY1; i++ ) {
            if( i != LRU_INDEX ) {
                assertThat( cache.get( i ), is( (Object) i ) );
            }
        }

        // Add new object to cache
        int newKey = CAPACITY1;
        int newValue = CAPACITY1;
        cache.put( newKey, newValue );
        assertThat( cache.get( newKey ), is( (Object) newValue ) );

        // Last recently used element from level-1 is still accessible on level-2
        Path lruPath = Paths.get( DiskLruCacheTest.BASE_PATH, DiskLruCache.getFileName( LRU_INDEX ) );
        assertTrue( cache.containsKey( LRU_INDEX ) );
        assertTrue( Files.exists( lruPath ) );
    }
}
