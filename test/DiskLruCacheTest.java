import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

public class DiskLruCacheTest extends CacheTest
{
    public static final int CAPACITY = 8;
    public static final String BASE_PATH = "/tmp/simple-cache";

    @Before
    public void setUp() throws IOException
    {
        cache = DiskLruCache.create( CAPACITY, BASE_PATH );
    }

    @Test
    public void testLastRecentlyUsedRemove() throws IOException
    {
        final int LRU_INDEX = 3;

        fillCache();

        // Call get() on all element except one
        for( int i = 0; i < CAPACITY; i++ ) {
            if( i != LRU_INDEX ) {
                Path path = Paths.get( BASE_PATH, DiskLruCache.getFileName( i ) );
                assertTrue( Files.isReadable( path ) );
                assertThat( cache.get( i ), is( (Object) i ) );
            }
        }

        // Add new object to cache
        putToCache( 99999, new byte[1024] );

        // Last recently used element is removed from cache and from disk
        Path lruPath = Paths.get( BASE_PATH, DiskLruCache.getFileName( LRU_INDEX ) );
        assertFalse( cache.containsKey( LRU_INDEX ) );
        assertFalse( Files.exists( lruPath ) );
    }
}
