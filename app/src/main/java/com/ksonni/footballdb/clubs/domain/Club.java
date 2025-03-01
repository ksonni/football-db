package com.ksonni.footballdb.clubs.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
@NoArgsConstructor(force = true)
@Builder
@AllArgsConstructor
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
