package com.ksonni.footballdb.queryparser;

import com.ksonni.footballdb.queryparser.keys.InvalidQueryKeyException;
import com.ksonni.footballdb.queryparser.keys.SortQueryKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SortQueryKeyTests {

    @Test
    void parsesDescendingQueryKey() throws InvalidQueryKeyException {
        final var key = new SortQueryKey("desc:someField");
        Assertions.assertTrue(key.isDescending());
        Assertions.assertEquals("someField", key.getField());
    }

    @Test
    void parsesAscendingQueryKey() throws InvalidQueryKeyException {
        final var key = new SortQueryKey("someField");
        Assertions.assertFalse(key.isDescending());
        Assertions.assertEquals("someField", key.getField());
    }

}
