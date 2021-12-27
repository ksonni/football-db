package com.ksonni.footballdb.players.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "players")
public class Player {

    /**
     * Max height of a player in cm.
     */
    public static final int MAX_HEIGHT = 500;

    /**
     * Max reputation.
     */
    public static final int MAX_REPUTATION = 10;

    /**
     * Max weight of a player in kg.
     */
    public static final int MAX_WEIGHT = 500;

    @Id
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

    private String position;

    private Integer birthYear;

    private String countryCode;

    private String image;

}
