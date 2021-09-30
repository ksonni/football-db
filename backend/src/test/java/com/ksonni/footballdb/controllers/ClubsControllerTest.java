package com.ksonni.footballdb.controllers;

import com.ksonni.footballdb.domain.Club;
import com.ksonni.footballdb.lib.HttpUtils;
import com.ksonni.footballdb.queryapi.Query;
import com.ksonni.footballdb.repositories.ClubsRepository;
import com.ksonni.footballdb.testutils.HttpTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClubsControllerTest {

    @Mock
    ClubsRepository clubsRepository;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    ClubsController controller;

    @Test
    void enumerateClubs() throws HttpUtils.QueryParseException {
        HttpTestUtils.mockQuery(request,"name=Manchester");

        controller.enumerateClubs(request);

        verify(clubsRepository).findAll(ArgumentMatchers.<Query<Club>>any());
    }

    @Test
    void enumerateClubsInvalidQuery() {
        HttpTestUtils.mockQuery(request,"name::=Manchester");

        assertThrows(HttpUtils.QueryParseException.class, () -> {
            controller.enumerateClubs(request);
        });
    }
}