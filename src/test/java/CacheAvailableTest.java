import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;


/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 */
@RunWith(Arquillian.class)
public class CacheAvailableTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(new StringAsset("Dependencies: org.infinispan export"), "MANIFEST.MF");
    }

    @Resource(name = "java:jboss/infinispan/container/app-caches")
    EmbeddedCacheManager appCacheContainer;

    @Test
    public void mensaCacheAvailable() {
        Cache cache = appCacheContainer.getCache("mensaCache", false);
        final String key = "testKey";
        final String value = "testValue";

        Assert.assertNotNull(cache);
        cache.put(key, value);
        Assert.assertEquals(value, cache.get(key));
        cache.remove(key);
        Assert.assertNull(cache.get(key));
    }
}
