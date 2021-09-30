package com.ksonni.footballdb.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor(force = true)
@Table(name = "clubs")
public class Club {

    @Id
    private String id;

    private String name;

    private String leagueId;

    private Integer overallRating;

    private Integer attackRating;

    private Integer midfieldRating;

    private Integer defenseRating;

    private Integer transferBudget;

    private Integer domesticPrestige;

    private Integer internationalPrestige;

}
