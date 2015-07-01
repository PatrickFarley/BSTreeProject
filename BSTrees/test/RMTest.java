package test;

import impl.RedBlackTreeMap;

public class RMTest extends MapTest {

    protected void reset() {
        testMap = new RedBlackTreeMap<String, String>();

    }

}
