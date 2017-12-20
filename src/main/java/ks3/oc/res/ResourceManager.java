package ks3.oc.res;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class ResourceManager {
    
    private static final Logger LOGGER = Logger.getLogger(ResourceManager.class);
    
    private final Map<String, Image> figureSetIcons = new HashMap<>();
    private final Map<String, Image> wrappedFigureSetIcons = Collections.unmodifiableMap(figureSetIcons);
    
    public ResourceManager() {
        loadAvailableFigureSetIcons();
    }
    
    public void loadAvailableFigureSetIcons() {
        try (BufferedReader figureSetList = openDescriptor("/img/figures/list.txt")) {
            figureSetList.lines().forEachOrdered(this::loadFigureSetIcon);
        } catch (IOException ex) {
            LOGGER.error("Error loading figure set icons", ex);
        }
    }
    
    private BufferedReader openDescriptor(String path) {
        return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path)));
    }
    
    private void loadFigureSetIcon(String setName) {
        try {
            Image icon = ImageIO.read(getClass().getResource("/img/figures/" + setName + "/icon.gif"));
            figureSetIcons.put(setName, icon);
        } catch (IOException ex) {
            LOGGER.error("Error loading icon of figure set'" + setName + "'", ex);
        }
    }

    public Map<String, Image> getFigureSetIcons() {
        return wrappedFigureSetIcons;
    }

}
