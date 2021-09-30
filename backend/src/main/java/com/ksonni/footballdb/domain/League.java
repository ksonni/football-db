package com.ksonni.footballdb.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor(force = true)
@Table(name = "leagues")
public class League {
    @Id
    private final String id;

    private final String name;
}
