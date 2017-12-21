package ks3.oc.res;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ResourceManager {

    class MissingResourcesError extends Error {

        public MissingResourcesError(String message) {
            super(message);
        }

        public MissingResourcesError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(ResourceManager.class);

    private final String figureSetPath;
    private final String boardPath;

    private final Map<String, Image> figureSetIcons = new HashMap<>();
    private final Map<String, Image> wrappedFigureSetIcons = Collections.unmodifiableMap(figureSetIcons);
    private final Map<String, FigureSet> figureSets = new HashMap<>();

    private final Map<String, Image> boardIcons = new HashMap<>();
    private final Map<String, Image> wrappedBoardIcons = Collections.unmodifiableMap(boardIcons);
    private final Map<String, Image> boards = new HashMap<>();

    private String figureSetName;
    private String boardName;

    public ResourceManager(String figureSetPath, String boardPath, String figureSetName, String boardName) {
        this.figureSetPath = figureSetPath;
        this.boardPath = boardPath;

        loadAvailableFigureSetIcons();
        loadAvailableBoardIcons();

        if (figureSetIcons.isEmpty() || boardIcons.isEmpty()) {
            throw new MissingResourcesError("Failed to load required resources");
        }

        loadFigureSet(figureSetName);
        loadBoard(boardName);
    }

    private void loadAvailableFigureSetIcons() {
        processResourceList(figureSetPath + "list.txt", this::loadFigureSetIcon);
    }

    private void loadAvailableBoardIcons() {
        processResourceList(boardPath + "list.txt", this::loadBoardIcon);
    }

    private void loadFigureSet(String figureSetName) {
        String imagePath;
        Image[][] figureImages = new Image[4][6];
        for (int colorIdx = 0; colorIdx < 4; colorIdx++) {
            for (int typeIdx = 0; typeIdx < 6; typeIdx++) {
                imagePath = figureSetPath + figureSetName + "/" + colorIdx + typeIdx + ".gif";
                figureImages[colorIdx][typeIdx] = tryToLoadRequiredResource(imagePath);
            }
        }
        this.figureSetName = figureSetName;
        figureSets.put(figureSetName, new FigureSet(figureImages));
    }

    private void loadBoard(String boardName) {
        this.boardName = boardName;
        String resourcePath = boardPath + boardName + ".gif";
        boards.put(boardName, tryToLoadRequiredResource(resourcePath));
    }

    private Image tryToLoadRequiredResource(String resourcePath) {
        try {
            return ImageIO.read(getClass().getResource(resourcePath));
        } catch (IOException | IllegalArgumentException ex) {
            throw new MissingResourcesError("Failed to load required resource " + resourcePath, ex);
        }
    }

    private void processResourceList(String path, Consumer<String> processor) {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                LOGGER.error("Resource not found: " + path);
            } else {
                new BufferedReader(new InputStreamReader(inputStream)).lines().forEachOrdered(processor);
            }
        } catch (IOException ex) {
            LOGGER.error("Error closing resource " + path, ex);
        }
    }

    private void loadFigureSetIcon(String figureSetName) {
        loadIcon(figureSetIcons, figureSetPath, figureSetName, "/icon.gif");
    }

    private void loadBoardIcon(String boardName) {
        loadIcon(boardIcons, boardPath, boardName, "_icon.gif");
    }

    private void loadIcon(Map<String, Image> container, String path, String resourceName, String postfix) {
        String resourcePath = path + resourceName + postfix;
        try {
            Image icon = ImageIO.read(getClass().getResource(resourcePath));
            container.put(resourceName, icon);
        } catch (IOException ex) {
            LOGGER.error("Error loading icon from " + resourcePath, ex);
        }
    }

    public Map<String, Image> getFigureSetIcons() {
        return wrappedFigureSetIcons;
    }

    public Map<String, Image> getBoardIcons() {
        return wrappedBoardIcons;
    }

    public FigureSet getFigureSet() {
        return figureSets.get(figureSetName);
    }

    public Object getBoard() {
        return boards.get(boardName);
    }
}
