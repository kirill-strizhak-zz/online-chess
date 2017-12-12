package ks3.oc.logic;

import ks3.oc.Protocol;

public class LogicCastlingWhenBlackTest extends LogicCastlingWhenWhiteTest {

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
