import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

public class BackedLruCacheTest extends CacheTest
{
    public static final int CAPACITY1 = 4;
    public static final int CAPACITY2 = 8;
    public static final String BASE_PATH = "/tmp/simple-cache";

    @Before
    public void setUp() throws IOException
    {
        cache = BackedLruCache.create( CAPACITY1, CAPACITY2, BASE_PATH );
        assertThat( cache.getCapacity(), is( CAPACITY1 + CAPACITY2 ) );
    }

    @Test
    public void testLastRecentlyUsedRemove() throws IOException
    {
        final int LRU_INDEX = 2;

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
        putToCache( 99991, new byte[1024] );

        // Last recently used element from level-1 is still accessible on level-2
        Path lruPath = Paths.get( DiskLruCacheTest.BASE_PATH, DiskLruCache.getFileName( LRU_INDEX ) );
        assertTrue( cache.containsKey( LRU_INDEX ) );
        assertTrue( Files.exists( lruPath ) );
    }
}
