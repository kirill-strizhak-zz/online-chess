package ks3.oc.logic;

@FunctionalInterface
interface BoundaryValidator {
    boolean test(int col, int row);
}
