package com.ksonni.footballdb.query;

import com.ksonni.footballdb.generated.ql.QLPlayerSort;
import com.ksonni.footballdb.generated.ql.QLSort;
import com.ksonni.footballdb.generated.ql.QLSortDirection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DefaultSortParserTests {

    private final int maxResults = 10;

    @Test
    void testDecodesSortsInOrder() throws SortParseException {
        final int maxComponents = 10;
        final var parser = new DefaultSortParser<QLPlayerSort>(maxComponents, maxResults);

        final var sort = QLPlayerSort.builder()
            .setAttackingWorkRate(new QLSort(1, QLSortDirection.ASC))
            .setContractStartYear(new QLSort(2, QLSortDirection.DESC))
            .build();

        final var actual = parser.parseComponents(sort);
        final var expected = List.of(
            new SortComponent("attackingWorkRate", 1, false),
            new SortComponent("contractStartYear", 2, true)
        );

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testThrowsIfSortExceedsComponentLimit() {
        final int maxComponents = 1;
        final var parser = new DefaultSortParser<QLPlayerSort>(maxComponents, maxResults);

        final var sort = QLPlayerSort.builder()
            .setAttackingWorkRate(new QLSort(1, QLSortDirection.ASC))
            .setContractStartYear(new QLSort(2, QLSortDirection.DESC))
            .build();

        Assertions.assertThrows(SortParseException.class, () -> parser.parseComponents(sort));
    }
}
