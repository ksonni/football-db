package com.ksonni.footballdb.clubs;

import com.ksonni.footballdb.queryparser.Query;
import com.ksonni.footballdb.queryparser.QueryParseException;
import com.ksonni.footballdb.queryparser.QueryParser;
import com.ksonni.footballdb.testutils.HttpTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClubsControllerTest {

    @Mock
    ClubsRepository clubsRepository;

    @Mock
    HttpServletRequest request;

    @Mock
    QueryParser<Club> queryParser;

    @InjectMocks
    ClubsController controller;

    @Test
    void enumerateClubs() throws QueryParseException {
        HttpTestUtils.mockQuery(request,"name=Manchester");

        controller.enumerateClubs(request);

        verify(clubsRepository).findAll(ArgumentMatchers.<Query<Club>>any());
    }

    @Test
    void enumerateClubsInvalidQuery() throws QueryParseException {
        HttpTestUtils.mockQuery(request,"name::=Manchester");
        given(queryParser.parse(anyString())).willThrow(QueryParseException.class);

        assertThrows(QueryParseException.class, () -> {
            controller.enumerateClubs(request);
        });
    }
}
