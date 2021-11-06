package com.ksonni.footballdb.players;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor(force = true)
@Table(name = "players")
public class Player {
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

    private String preferredFoot;

    private Integer reputation;

    private String attackingWorkRate;

    private String defensiveWorkRate;

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
