import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

public class BackedCacheTest extends CacheTest
{
    public static final int CAPACITY1 = 8;
    public static final int CAPACITY2 = 16;
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
        Path path = Paths.get( BASE_PATH, DiskCache.getFileName( LRU_INDEX ) );
        assertTrue( cache.containsKey( LRU_INDEX ) );
        assertTrue( Files.exists( path ) );
    }

    @Test
    public void testMostRecentlyUsedRemove() throws IOException
    {
        cache = BackedCache.create( Cache.Strategy.MRU, CAPACITY1, CAPACITY2, BASE_PATH );
        final int MRU_INDEX = 5;

        // Fill level-1 cache
        for( int i = 0; i < CAPACITY1; i++ ) {
            cache.put( i, i );
        }

        // Call get() on one element
        assertThat( cache.get( MRU_INDEX ), is( (Object) MRU_INDEX ) );

        // Add new object to cache
        putToCache( 99991, new byte[1024] );

        // Most recently used element from level-1 is still accessible on level-2
        Path path = Paths.get( BASE_PATH, DiskCache.getFileName( MRU_INDEX ) );
        assertTrue( cache.containsKey( MRU_INDEX ) );
        assertTrue( Files.exists( path ) );
    }
}
