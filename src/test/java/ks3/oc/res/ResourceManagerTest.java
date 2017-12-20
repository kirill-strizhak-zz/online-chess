package ks3.oc.res;

import java.awt.Image;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ResourceManagerTest {
    
    @Test
    public void testLoadAvailableFigureSetIcons() {
        ResourceManager resourceManager = new ResourceManager();
        Map<String, Image> icons = resourceManager.getFigureSetIcons();
        Assert.assertEquals(2, icons.size());
        Assert.assertTrue(icons.containsKey("default"));
        Assert.assertTrue(icons.containsKey("alternative"));
    }

}
