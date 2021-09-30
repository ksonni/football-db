package com.ksonni.footballdb.queryapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SortQueryKeyTests {

    @Test
    void parsesDescendingQueryKey() throws InvalidQueryKeyException {
        var key = new SortQueryKey("desc:someField");
        assertTrue(key.isDescending());
        assertEquals("someField", key.getField());
    }

    @Test
    void parsesAscendingQueryKey() throws InvalidQueryKeyException  {
        var key = new SortQueryKey("someField");
        assertFalse(key.isDescending());
        assertEquals("someField", key.getField());
    }

}
