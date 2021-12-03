package com.ksonni.footballdb.players;

import com.ksonni.footballdb.players.domain.Player;
import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import com.ksonni.footballdb.players.services.PlayerQueryParser;
import com.ksonni.footballdb.queryparser.Query;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.queryparser.components.InvalidQueryValueException;
import com.ksonni.footballdb.queryparser.keys.FilterQueryKey;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayersQueryParserTests {

    private QueryParser<Player> queryParser = new PlayerQueryParser(Player.class);

    @Test
    void constructsQueries() throws QueryParseException {
        var queryStr = "attackingWorkRate=1&defensiveWorkRate=2&preferredFoot=LEFT";
        Query<Player> query = queryParser.parse(queryStr);

        assertEquals(Arrays.asList(
            new WorkRate.WorkRateFilterQueryComponent(new FilterQueryKey("attackingWorkRate"), "1"),
            new WorkRate.WorkRateFilterQueryComponent(new FilterQueryKey("defensiveWorkRate"), "2"),
            new Side.SideFilterQueryComponent(new FilterQueryKey("preferredFoot"), "LEFT")
        ), query.getFilterQueryComponents());
    }

    @Test
    void constructsQueriesWithModifiers() throws QueryParseException {
        var queryStr = "lt:attackingWorkRate=1&gt:defensiveWorkRate=2&preferredFoot=LEFT";
        Query<Player> query = queryParser.parse(queryStr);

        assertEquals(Arrays.asList(
                new WorkRate.WorkRateFilterQueryComponent(new FilterQueryKey("lt:attackingWorkRate"), "1"),
                new WorkRate.WorkRateFilterQueryComponent(new FilterQueryKey("gt:defensiveWorkRate"), "2"),
                new Side.SideFilterQueryComponent(new FilterQueryKey("preferredFoot"), "LEFT")
        ), query.getFilterQueryComponents());
    }

    @Test
    void rejectsQueriesWithInvalidEnumValues() {
        assertThrows(InvalidQueryValueException.class, () -> {
            queryParser.parse("attackingWorkRate=-1");
        });
        assertThrows(InvalidQueryValueException.class, () -> {
            queryParser.parse("defensiveWorkRate=xyz");
        });
        assertThrows(InvalidQueryValueException.class, () -> {
            queryParser.parse("preferredFoot=Unknown");
        });
    }

}
