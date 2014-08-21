import org.junit.Assert;

import static org.hamcrest.core.Is.is;

public class CacheTest extends Assert
{
    protected Cache<Integer, Object> cache;

    public void fillCache()
    {
        for( int i = 0; i < cache.getCapacity(); i++ ) {
            cache.put( i, i );
        }
        assertThat( cache.getSize(), is( cache.getCapacity() ) );
    }

    public void putToCache( Integer key, Object val )
    {
        cache.put( key, val );
        assertThat( cache.get( key ), is( val ) );
    }
}
