package ks3.oc.logic;

import ks3.oc.Protocol;

public class Logic_CastlingWhenBlackTest extends Logic_CastlingWhenWhiteTest {

    @Override
    protected int col() {
        return 3;
    }

    @Override
    public int getMyColor() {
        return Protocol.BLACK;
    }

    @Override
    public int getOppColor() {
        return Protocol.WHITE;
    }
}
