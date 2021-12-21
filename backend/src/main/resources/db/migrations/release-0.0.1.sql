--liquibase formatted sql

--changeset root:release-0.0.1-c1
CREATE TABLE `leagues` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);
--rollback DROP TABLE leagues;

--changeset root:release-0.0.1-c2
CREATE TABLE `clubs` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) NOT NULL,
  `league_id` varchar(36) NOT NULL,
  `overall_rating` int DEFAULT NULL,
  `attack_rating` int DEFAULT NULL,
  `midfield_rating` int DEFAULT NULL,
  `defense_rating` int DEFAULT NULL,
  `transfer_budget` int DEFAULT NULL,
  `domestic_prestige` int DEFAULT NULL,
  `international_prestige` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_teams_leagues_idx` (`league_id`),
  CONSTRAINT `fk_teams_leagues` FOREIGN KEY (`league_id`) REFERENCES `leagues` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);
--rollback DROP TABLE clubs;

--changeset root:release-0.0.1-c3
CREATE TABLE `players` (
  `id` varchar(36) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `height` int DEFAULT NULL,
  `weight` int DEFAULT NULL,
  `overall` int DEFAULT NULL,
  `value_euro` int DEFAULT NULL,
  `wage_euro` int DEFAULT NULL,
  `contract_end_year` int DEFAULT NULL,
  `contract_start_year` int DEFAULT NULL,
  `preferred_foot` varchar(255) DEFAULT NULL,
  `reputation` int DEFAULT NULL,
  `attacking_work_rate` int DEFAULT NULL,
  `defensive_work_rate` int DEFAULT NULL,
  `shooting_total` int DEFAULT NULL,
  `passing_total` int DEFAULT NULL,
  `dribbling_total` int DEFAULT NULL,
  `defending_total` int DEFAULT NULL,
  `heading_accuracy` int DEFAULT NULL,
  `penalties` int DEFAULT NULL,
  `club_id` varchar(36) DEFAULT NULL,
  `squad_number` int DEFAULT NULL,
  `position` varchar(255) DEFAULT NULL,
  `birth_year` int DEFAULT NULL,
  `country_code` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_players_teams_idx` (`club_id`),
  CONSTRAINT `fk_players_teams` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);
--rollback DROP TABLE players;

--changeset root:release-0.0.1-c4
CREATE TABLE `users` (
  `id` VARCHAR(36) NOT NULL,
  `email_id` VARCHAR(100) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_id_UNIQUE` (`email_id` ASC) VISIBLE
);
--rollback DROP TABLE users;

--changeset root:release-0.0.1-c5
ALTER TABLE `users` ADD COLUMN `role` VARCHAR(40) NULL AFTER `password`;
--rollback ALTER TABLE `users` DROP COLUMN `role`;

--changeset root:release-0.0.1-c6
ALTER TABLE `clubs`
CHANGE COLUMN `overall_rating` `overall_rating` INT NOT NULL DEFAULT 0,
CHANGE COLUMN `attack_rating` `attack_rating` INT NOT NULL DEFAULT 0,
CHANGE COLUMN `midfield_rating` `midfield_rating` INT NOT NULL DEFAULT 0,
CHANGE COLUMN `defense_rating` `defense_rating` INT NOT NULL DEFAULT 0,
CHANGE COLUMN `transfer_budget` `transfer_budget` INT NOT NULL DEFAULT 0,
CHANGE COLUMN `domestic_prestige` `domestic_prestige` INT NOT NULL DEFAULT 0,
CHANGE COLUMN `international_prestige` `international_prestige` INT NOT NULL DEFAULT 0;
-- rollback ALTER TABLE `clubs`
-- rollback CHANGE COLUMN `overall_rating` `overall_rating` INT NULL DEFAULT NULL,
-- rollback CHANGE COLUMN `attack_rating` `attack_rating` INT NULL DEFAULT NULL,
-- rollback CHANGE COLUMN `midfield_rating` `midfield_rating` INT NULL DEFAULT NULL,
-- rollback CHANGE COLUMN `defense_rating` `defense_rating` INT NULL DEFAULT NULL,
-- rollback CHANGE COLUMN `transfer_budget` `transfer_budget` INT NULL DEFAULT NULL,
-- rollback CHANGE COLUMN `domestic_prestige` `domestic_prestige` INT NULL DEFAULT NULL,
-- rollback CHANGE COLUMN `international_prestige` `international_prestige` INT NULL DEFAULT NULL;
