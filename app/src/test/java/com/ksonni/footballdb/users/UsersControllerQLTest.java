package com.ksonni.footballdb.users;

import com.ksonni.footballdb.config.GraphQLConfig;
import com.ksonni.footballdb.generated.ql.QLUser;
import com.ksonni.footballdb.generated.ql.QLUserFilter;
import com.ksonni.footballdb.generated.ql.QLUserSort;
import com.ksonni.footballdb.query.FilterParser;
import com.ksonni.footballdb.query.PageResult;
import com.ksonni.footballdb.query.SortParser;
import com.ksonni.footballdb.users.domain.User;
import com.ksonni.footballdb.users.services.UsersMapperImpl;
import com.ksonni.footballdb.users.services.UsersRepository;
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

@GraphQlTest(UsersControllerQL.class)
@Import({GraphQLConfig.class, UsersMapperImpl.class})
class UsersControllerQLTest {
    @Autowired
    private GraphQlTester graphQlTester;
    @MockitoBean
    private UsersRepository usersRepository;
    @MockitoBean
    private FilterParser<User, QLUserFilter> usersFilterParser;
    @MockitoBean
    private SortParser<QLUserSort> usersSortParser;

    @Test
    void testUsersPath() {
        // Setup
        final var users = Arrays.asList(
            User.builder().id("id").emailId("Some user").build(),
            User.builder().id("id2").emailId("Some user 2").build()
        );
        final var result = new PageResult<>(users, users.size(), users.size(), users.size());
        BDDMockito.given(usersRepository.findAllResults(Mockito.any(), Mockito.any())).willReturn(result);

        // Execute
        final var contentPath = "users.content";
        final var response = graphQlTester.document("query { users { content { id emailId } } }")
            .execute().path(contentPath).entityList(QLUser.class).get();

        // Verify
        Assertions.assertEquals(users.size(), response.size());
        for (int i = 0; i < users.size(); i++) {
            Assertions.assertEquals(users.get(i).getId(), response.get(i).getId());
            Assertions.assertEquals(users.get(i).getEmailId(), response.get(i).getEmailId());
        }
    }

    @Test
    void testUserPath() {
        // Setup
        final var user = User.builder().id("id").emailId("Some user").build();
        BDDMockito.given(usersRepository.findById("id")).willReturn(Optional.of(user));

        // Execute
        final var response = graphQlTester.document("query { user(id: \"id\") { id emailId } }")
            .execute().path("user").entity(QLUser.class).get();

        // Verify
        Assertions.assertEquals(user.getId(), response.getId());
        Assertions.assertEquals(user.getEmailId(), response.getEmailId());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(usersRepository, usersFilterParser, usersSortParser);
    }
}
