package ks3.oc.logic;

class BoundaryValidators {

    public static final BoundaryValidator TOP_LEFT = (col, row) -> col >= 0 && row >= 0;
    public static final BoundaryValidator TOP_RIGHT = (col, row) -> col <= 7 && row >= 0;
    public static final BoundaryValidator BOTTOM_LEFT = (col, row) -> col >= 0 && row <= 7;
    public static final BoundaryValidator BOTTOM_RIGHT = (col, row) -> col <= 7 && row <= 7;

    private BoundaryValidators() {

    }
}
