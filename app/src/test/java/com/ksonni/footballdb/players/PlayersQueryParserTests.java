package com.ksonni.footballdb.players;

import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.domain.Position;
import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import com.ksonni.footballdb.players.services.PlayerQueryParser;
import com.ksonni.footballdb.queryparser.Query;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.queryparser.components.InvalidQueryValueException;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class PlayersQueryParserTests {

    private QueryParser<Player> queryParser = new PlayerQueryParser();

    @Test
    void constructsQueries() throws QueryParseException {
        final var queryStr = "attackingWorkRate=1&defensiveWorkRate=2&preferredFoot=LEFT&position=CAM";
        final Query<Player> query = queryParser.parse(queryStr);

        Assertions.assertEquals(Arrays.asList(
                new WorkRate.WorkRateFilterQueryComponent(new FilterQueryKey("attackingWorkRate"), "1"),
                new WorkRate.WorkRateFilterQueryComponent(new FilterQueryKey("defensiveWorkRate"), "2"),
                new Side.SideFilterQueryComponent(new FilterQueryKey("preferredFoot"), "LEFT"),
                new Position.PositionFilterQueryComponent(new FilterQueryKey("position"), "CAM")
        ), query.getFilterQueryComponents());
    }

    @Test
    void constructsQueriesWithModifiers() throws QueryParseException {
        final var queryStr = "lt:attackingWorkRate=1&gt:defensiveWorkRate=2&lt:preferredFoot=LEFT&lt:position=CAM";
        final Query<Player> query = queryParser.parse(queryStr);

        Assertions.assertEquals(Arrays.asList(
                new WorkRate.WorkRateFilterQueryComponent(new FilterQueryKey("lt:attackingWorkRate"), "1"),
                new WorkRate.WorkRateFilterQueryComponent(new FilterQueryKey("gt:defensiveWorkRate"), "2"),
                new Side.SideFilterQueryComponent(new FilterQueryKey("lt:preferredFoot"), "LEFT"),
                new Position.PositionFilterQueryComponent(new FilterQueryKey("lt:position"), "CAM")
        ), query.getFilterQueryComponents());
    }

    @Test
    void rejectsQueriesWithInvalidEnumValues() {
        Assertions.assertThrows(InvalidQueryValueException.class, () -> {
            queryParser.parse("attackingWorkRate=-1");
        });
        Assertions.assertThrows(InvalidQueryValueException.class, () -> {
            queryParser.parse("defensiveWorkRate=xyz");
        });
        Assertions.assertThrows(InvalidQueryValueException.class, () -> {
            queryParser.parse("preferredFoot=Unknown");
        });
        Assertions.assertThrows(InvalidQueryValueException.class, () -> {
            queryParser.parse("position=BAD_POSITION");
        });
    }

}
