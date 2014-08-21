import org.junit.Assert;

import static org.hamcrest.core.Is.is;

public abstract class CacheTest extends Assert
{
    public abstract Cache getCache();

    @SuppressWarnings( "unchecked" )
    public void fillCache()
    {
        for( int i = 0; i < getCache().getCapacity(); i++ ) {
            getCache().put( i, i );
        }
        assertThat( getCache().getSize(), is( getCache().getCapacity() ) );
    }

    @SuppressWarnings( "unchecked" )
    public void putToCache( Integer key, Object val )
    {
        getCache().put( key, val );
        assertThat( getCache().get( key ), is( val ) );
    }
}
