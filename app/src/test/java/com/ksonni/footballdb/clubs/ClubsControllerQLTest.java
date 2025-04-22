package com.ksonni.footballdb.clubs;

import com.ksonni.footballdb.clubs.domain.Club;
import com.ksonni.footballdb.clubs.services.ClubsMapperImpl;
import com.ksonni.footballdb.clubs.services.ClubsRepository;
import com.ksonni.footballdb.config.GraphQLConfig;
import com.ksonni.footballdb.generated.ql.QLClub;
import com.ksonni.footballdb.generated.ql.QLClubFilter;
import com.ksonni.footballdb.generated.ql.QLClubSort;
import com.ksonni.footballdb.query.FilterParser;
import com.ksonni.footballdb.query.PageResult;
import com.ksonni.footballdb.query.SortParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.Optional;

@GraphQlTest(ClubsControllerQL.class)
@Import({GraphQLConfig.class, ClubsMapperImpl.class})
class ClubsControllerQLTest {
    @Autowired
    private GraphQlTester graphQlTester;
    @MockitoBean
    private ClubsRepository clubsRepository;
    @MockitoBean
    private FilterParser<Club, QLClubFilter> clubsFilterParser;
    @MockitoBean
    private SortParser<QLClubSort> clubsSortParser;

    @Test
    void testClubsPath() {
        // Setup
        final var clubs = Arrays.asList(
            Club.builder().id("id").name("Some club").build(),
            Club.builder().id("id2").name("Some club 2").build()
        );
        final var result = new PageResult<>(clubs, clubs.size(), clubs.size(), clubs.size());
        BDDMockito.given(clubsRepository.findAllResults(Mockito.any(), Mockito.any())).willReturn(result);

        // Execute
        final var contentPath = "clubs.content";
        final var response = graphQlTester.document("query { clubs { content { id name } } }")
            .execute().path(contentPath).entityList(QLClub.class).get();

        // Verify
        Assertions.assertEquals(clubs.size(), response.size());
        for (int i = 0; i < clubs.size(); i++) {
            Assertions.assertEquals(clubs.get(i).getId(), response.get(i).getId());
            Assertions.assertEquals(clubs.get(i).getName(), response.get(i).getName());
        }
    }

    @Test
    void testClubPath() {
        // Setup
        final var club = Club.builder().id("id").name("Some club").build();
        BDDMockito.given(clubsRepository.findById("id")).willReturn(Optional.of(club));

        // Execute
        final var response = graphQlTester.document("query { club(id: \"id\") { id name } }")
            .execute().path("club").entity(QLClub.class).get();

        // Verify
        Assertions.assertEquals(club.getId(), response.getId());
        Assertions.assertEquals(club.getName(), response.getName());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(clubsRepository, clubsFilterParser, clubsSortParser);
    }
}
