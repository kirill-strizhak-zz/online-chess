package ks3.oc.res;

import java.awt.*;

public class FigureSet {

    private final Image[][] figureImages;

    FigureSet(Image[][] figureImages) {
        this.figureImages = figureImages;
    }

    public Image getImage(int colorId, int typeId) {
        return figureImages[colorId][typeId];
    }
}
