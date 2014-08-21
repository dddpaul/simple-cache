import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

public class DiskCacheTest extends CacheTest
{
    public static final int CAPACITY = 8;
    public static final String BASE_PATH = "/tmp/simple-cache";

    @Test
    public void testCapacity() throws IOException
    {
        cache = DiskCache.create( Cache.Strategy.LRU, CAPACITY, BASE_PATH );
        fillCache();
        putToCache( 9998, "New element" );
        putToCache( 9999, new byte[1024] );
        assertThat( cache.getSize(), is( cache.getCapacity() ));
    }

    @Test
    public void testLastRecentlyUsedRemove() throws IOException
    {
        cache = DiskCache.create( Cache.Strategy.LRU, CAPACITY, BASE_PATH );
        final int LRU_INDEX = 3;

        fillCache();

        // Call get() on all element except one
        for( int i = 0; i < CAPACITY; i++ ) {
            if( i != LRU_INDEX ) {
                Path path = ( (DiskCache<Integer>) cache ).getPath( i );
                assertTrue( Files.isReadable( path ) );
                assertThat( cache.get( i ), is( (Object) i ) );
            }
        }

        // Add new object to cache
        putToCache( 99999, new byte[1024] );

        // Last recently used element is removed from cache and from disk
        Path path = ( (DiskCache<Integer>) cache ).getPath( LRU_INDEX );
        assertFalse( cache.containsKey( LRU_INDEX ) );
        assertFalse( Files.exists( path ) );
    }

    @Test
    public void testMostRecentlyUsedRemove() throws IOException
    {
        cache = DiskCache.create( Cache.Strategy.MRU, CAPACITY, BASE_PATH );
        final int MRU_INDEX = 2;

        fillCache();

        // Call get() on one element
        Path path = ( (DiskCache<Integer>) cache ).getPath( MRU_INDEX );
        assertTrue( Files.isReadable( path ) );
        assertThat( cache.get( MRU_INDEX ), is( (Object) MRU_INDEX ) );

        // Add new object to cache
        putToCache( 99999, new byte[1024] );

        // Most recently used element is removed from cache and from disk
        path = ( (DiskCache<Integer>) cache ).getPath( MRU_INDEX );
        assertFalse( cache.containsKey( MRU_INDEX ) );
        assertFalse( Files.exists( path ) );
    }
}
