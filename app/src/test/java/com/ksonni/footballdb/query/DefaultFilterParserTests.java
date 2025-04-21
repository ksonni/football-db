package com.ksonni.footballdb.query;

import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.generated.ql.*;
import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.domain.Side;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

class DefaultFilterParserTests {
    private DefaultFilterParser<Player, QLPlayerFilter> parser;

    private final int maxComponents = 100;

    @BeforeEach
    void setup() {
        parser = new DefaultFilterParser<>(maxComponents);
    }

    @Test
    void testEqualsComparison() throws FilterParseException {
        assertIntComparison(QLIntFilter.Builder::setEq, FilterComponent.Comparison.EQUALS);
    }

    @Test
    void testNotEqualsComparison() throws FilterParseException {
        assertIntComparison(QLIntFilter.Builder::setNe, FilterComponent.Comparison.NOT_EQUALS);
    }

    @Test
    void testLessThanComparison() throws FilterParseException {
        assertIntComparison(QLIntFilter.Builder::setLt, FilterComponent.Comparison.LESS_THAN);
    }

    @Test
    void testGreaterThanComparison() throws FilterParseException {
        assertIntComparison(QLIntFilter.Builder::setGt, FilterComponent.Comparison.GREATER_THAN);
    }

    @Test
    void testLessThanEqualsComparison() throws FilterParseException {
        assertIntComparison(QLIntFilter.Builder::setLte, FilterComponent.Comparison.LESS_THAN_EQUALS);
    }

    @Test
    void testGreaterThanEqualsComparison() throws FilterParseException {
        assertIntComparison(QLIntFilter.Builder::setGte, FilterComponent.Comparison.GREATER_THAN_EQUALS);
    }

    @Test
    void testContainsComparison() throws FilterParseException {
        assertStringComparison(QLStringFilter.Builder::setContains, FilterComponent.Comparison.CONTAINS);
    }

    @Test
    void testStringEqualsComparison() throws FilterParseException {
        assertStringComparison(QLStringFilter.Builder::setEq, FilterComponent.Comparison.EQUALS);
    }

    @Test
    void testStringNotEqualsComparison() throws FilterParseException {
        assertStringComparison(QLStringFilter.Builder::setNe, FilterComponent.Comparison.NOT_EQUALS);
    }

    @Test
    void testDateComparison() throws FilterParseException {
        // Setup
        final var fileParser = new DefaultFilterParser<FileRegistration, QLFileRegistrationFilter>(maxComponents);
        final var date = ZonedDateTime.now();
        final var builder = QLDateTimeFilter.builder().setEq(date);
        final var filter = QLFileRegistrationFilter.builder().setCreated(builder.build()).build();

        // Execute
        final var actual = fileParser.parseComponents(filter);

        // Assert
        final var expected = List.of(
            new FilterComponent<FileRegistration>("created", FilterComponent.Comparison.EQUALS, date)
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testCustomValueDecoder() throws FilterParseException {
        // Setup
        final var decoder = new SideValueDecoder();
        parser.registerDecoder(QLSide.class, decoder);
        final var builder = QLSideFilter.builder().setEq(QLSide.LEFT);
        final var filter = QLPlayerFilter.builder().setPreferredFoot(builder.build()).build();

        // Execute
        final var actual = parser.parseComponents(filter);

        // Assert
        final var expected = List.of(
            new FilterComponent<Player>("preferredFoot", FilterComponent.Comparison.EQUALS, Side.LEFT)
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testThrowsIfValueDecoderIsNotRegistered() {
        // Setup
        final var builder = QLSideFilter.builder().setEq(QLSide.LEFT);
        final var filter = QLPlayerFilter.builder().setPreferredFoot(builder.build()).build();

        // Execute
        Assertions.assertThrows(FilterParseException.class, () -> parser.parseComponents(filter));
    }

    @Test
    void testThrowsIfQueryExceedsComponentLimit() {
        // Setup
        final var smallMaxComponents = 1;
        parser = new DefaultFilterParser<>(smallMaxComponents);
        final var builder = QLIntFilter.builder().setLt(0).setGt(0);
        final var filter = QLPlayerFilter.builder().setBirthYear(builder.build()).build();

        // Execute
        Assertions.assertThrows(FilterParseException.class, () -> parser.parseComponents(filter));
    }

    @Test
    void testMixedQuery() throws FilterParseException {
        parser.registerDecoder(QLSide.class, new SideValueDecoder());

        final int y1 = 2015;
        final int y2 = 2022;

        final var filter = QLPlayerFilter.builder()
            .setFullName(QLStringFilter.builder().setContains("John").build())
            .setPreferredFoot(QLSideFilter.builder().setEq(QLSide.LEFT).setNe(QLSide.RIGHT).build())
            .setBirthYear(QLIntFilter.builder().setLt(y1).setGt(y2).build())
            .build();

        // Execute
        final var actual = parser.parseComponents(filter);

        // Assert
        final var expected = List.of(
            new FilterComponent<Player>("fullName", FilterComponent.Comparison.CONTAINS, "John"),
            new FilterComponent<Player>("preferredFoot", FilterComponent.Comparison.EQUALS, Side.LEFT),
            new FilterComponent<Player>("preferredFoot", FilterComponent.Comparison.NOT_EQUALS, Side.RIGHT),
            new FilterComponent<Player>("birthYear", FilterComponent.Comparison.LESS_THAN, y1),
            new FilterComponent<Player>("birthYear", FilterComponent.Comparison.GREATER_THAN, y2)
        );
        Assertions.assertEquals(expected, actual);
    }

    // Helper
    private void assertIntComparison(
        final BiConsumer<QLIntFilter.Builder, Integer> buildFilter,
        final FilterComponent.Comparison comparison
    ) throws FilterParseException {
        // Setup
        final int year = 1960;
        final var builder = QLIntFilter.builder();
        buildFilter.accept(builder, year);
        final var filter = QLPlayerFilter.builder().setBirthYear(builder.build()).build();

        // Execute
        final var actual = parser.parseComponents(filter);

        // Assert
        final var expected = List.of(new FilterComponent<Player>("birthYear", comparison, year));
        Assertions.assertEquals(expected, actual);
    }

    private void assertStringComparison(
        final BiConsumer<QLStringFilter.Builder, String> buildFilter,
        final FilterComponent.Comparison comparison
    ) throws FilterParseException {
        // Setup
        final var name = "John";
        final var builder = QLStringFilter.builder();
        buildFilter.accept(builder, name);
        final var filter = QLPlayerFilter.builder().setFullName(builder.build()).build();

        // Execute
        final var actual = parser.parseComponents(filter);

        // Assert
        final var expected = List.of(new FilterComponent<Player>("fullName", comparison, name));
        Assertions.assertEquals(expected, actual);
    }
}

class SideValueDecoder implements ValueDecoder<QLSide, Side> {
    @Override
    public Comparable<Side> getValue(final QLSide value) {
        return switch (value) {
            case LEFT -> Side.LEFT;
            case RIGHT -> Side.RIGHT;
        };
    }
}
