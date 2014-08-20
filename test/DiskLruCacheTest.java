import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

public class DiskLruCacheTest extends Assert
{
    @Test
    public void testLastRecentlyUsedRemove() throws IOException
    {
        final int CAPACITY = 4;
        final int LRU_INDEX = 2;
        final String BASE_PATH = "/tmp/simple-cache";

        DiskLruCache<Integer> cache = DiskLruCache.create( CAPACITY, BASE_PATH );
        for( int i = 0; i < CAPACITY; i++ ) {
            cache.put( i, i );
        }

        // Call get() on all element except one
        for( int i = 0; i < CAPACITY; i++ ) {
            if( i != LRU_INDEX ) {
                Path path = Paths.get( BASE_PATH, DiskLruCache.getFileName( i ) );
                assertTrue( Files.isReadable( path ) );
                assertThat( cache.get( i ), is( (Object) i ) );
            }
        }

        // Add new object to cache
        int newKey = CAPACITY;
        int newValue = CAPACITY;
        cache.put( newValue, newValue );
        Path lruPath = Paths.get( BASE_PATH, DiskLruCache.getFileName( LRU_INDEX ) );

        // Last recently used element is removed from cache and from disk
        assertFalse( cache.containsKey( LRU_INDEX ) );
        assertFalse( Files.exists( lruPath ) );
        assertThat( cache.get( newKey ), is( (Object) newValue ) );
    }
}
