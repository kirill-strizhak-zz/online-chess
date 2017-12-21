package ks3.oc.res;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.Map;

public class ResourceManagerTest {

    private ResourceManager resourceManager;

    @Before
    public void setUp() {
        resourceManager = new ResourceManager("/img/figures/", "/img/boards/", "default", "default");
    }

    @Test(expected = ResourceManager.MissingResourcesError.class)
    public void testHandlesMissingResources() {
        resourceManager = new ResourceManager("/", "/", null, null);
    }
    
    @Test
    public void testLoadAvailableFigureSetIcons() {
        Map<String, Image> icons = resourceManager.getFigureSetIcons();
        Assert.assertEquals(3, icons.size());
        Assert.assertTrue(icons.containsKey("alternative"));
        Assert.assertTrue(icons.containsKey("broken"));
        Assert.assertTrue(icons.containsKey("default"));
    }

    @Test
    public void testLoadAvailableBoardIcons() {
        Map<String, Image> icons = resourceManager.getBoardIcons();
        Assert.assertEquals(2, icons.size());
        Assert.assertTrue(icons.containsKey("alternative"));
        Assert.assertTrue(icons.containsKey("default"));
    }

    @Test
    public void testLoadsFigureSet() {
        FigureSet figureSet = resourceManager.getFigureSet();
        Assert.assertNotNull(figureSet);
        Assert.assertNotNull(figureSet.getImage(0, 0));
    }

    @Test(expected = ResourceManager.MissingResourcesError.class)
    public void testHandlesInvalidFigureSet() {
        resourceManager = new ResourceManager("/img/figures/", "/img/boards/", "broken", "default");
    }

    @Test
    public void testLoadsBoard() {
        Assert.assertNotNull(resourceManager.getBoard());
    }

    @Test(expected = ResourceManager.MissingResourcesError.class)
    public void testHandlesInvalidBoard() {
        resourceManager = new ResourceManager("/img/figures/", "/img/boards/", "default", "no_such_name");
    }

    @Test
    public void testCanSelectAlternativeFigureSet() {
        resourceManager.selectFigureSet("alternative");
        Assert.assertEquals("alternative", resourceManager.getFigureSetName());
        FigureSet figureSet = resourceManager.getFigureSet();
        Assert.assertNotNull(figureSet);
        Assert.assertNotNull(figureSet.getImage(0, 0));
    }

    @Test
    public void testCanSelectAlternativeBoard() {
        resourceManager.selectBoard("alternative");
        Assert.assertEquals("alternative", resourceManager.getBoardName());
        Assert.assertNotNull(resourceManager.getBoard());
    }

}
