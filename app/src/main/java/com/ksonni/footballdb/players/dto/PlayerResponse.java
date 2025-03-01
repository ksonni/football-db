package com.ksonni.footballdb.players.dto;

import com.ksonni.footballdb.players.domain.Position;
import com.ksonni.footballdb.players.domain.Side;
import com.ksonni.footballdb.players.domain.WorkRate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerResponse {

    private String id;

    private String fullName;

    private Integer height;

    private Integer weight;

    private Integer overall;

    private Integer valueEuro;

    private Integer wageEuro;

    private Integer contractEndYear;

    private Integer contractStartYear;

    private Side preferredFoot;

    private Integer reputation;

    private WorkRate attackingWorkRate;

    private WorkRate defensiveWorkRate;

    private Integer shootingTotal;

    private Integer passingTotal;

    private Integer dribblingTotal;

    private Integer defendingTotal;

    private Integer headingAccuracy;

    private Integer penalties;

    private String clubId;

    private Integer squadNumber;

    private Position position;

    private Integer birthYear;

    private String countryCode;

    private String image;

}
