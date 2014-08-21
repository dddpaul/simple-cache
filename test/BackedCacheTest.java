import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

public class BackedCacheTest extends CacheTest
{
    public static final int CAPACITY1 = 4;
    public static final int CAPACITY2 = 8;
    public static final String BASE_PATH = "/tmp/simple-cache";

    @Test
    public void testCapacity() throws IOException
    {
        cache = BackedCache.create( Cache.Strategy.LRU, CAPACITY1, CAPACITY2, BASE_PATH );
        assertThat( cache.getCapacity(), is( CAPACITY1 + CAPACITY2 ) );
        fillCache();
        putToCache( 9998, "New element" );
        putToCache( 9999, new byte[1024] );
        assertThat( cache.getSize(), is( cache.getCapacity() ));
    }

    @Test
    public void testLastRecentlyUsedRemove() throws IOException
    {
        cache = BackedCache.create( Cache.Strategy.LRU, CAPACITY1, CAPACITY2, BASE_PATH );
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
        Path lruPath = Paths.get( DiskCacheTest.BASE_PATH, DiskLruCache.getFileName( LRU_INDEX ) );
        assertTrue( cache.containsKey( LRU_INDEX ) );
        assertTrue( Files.exists( lruPath ) );
    }
}
