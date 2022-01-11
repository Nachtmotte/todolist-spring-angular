SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema todolist
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `todolist` DEFAULT CHARACTER SET utf8 ;
USE `todolist` ;

-- -----------------------------------------------------
-- Table `todolist`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todolist`.`user` (
                                                 `id` INT NOT NULL AUTO_INCREMENT,
                                                 `firstname` VARCHAR(45) NOT NULL,
    `lastname` VARCHAR(45) NOT NULL,
    `email` VARCHAR(45) NOT NULL,
    `username` VARCHAR(20) NOT NULL,
    `password` VARCHAR(60) NOT NULL,
    `created` DATETIME NOT NULL,
    `verified` TINYINT(1) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `todolist`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todolist`.`role` (
                                                 `id` INT NOT NULL AUTO_INCREMENT,
                                                 `name` VARCHAR(30) NOT NULL,
    PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `todolist`.`user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todolist`.`user_role` (
                                                      `id` INT NOT NULL AUTO_INCREMENT,
                                                      `user_id` INT NOT NULL,
                                                      `role_id` INT NOT NULL,
                                                      PRIMARY KEY (`id`),
    INDEX `fk_user_role_user_idx` (`user_id` ASC) VISIBLE,
    INDEX `fk_user_role_role1_idx` (`role_id` ASC) VISIBLE,
    CONSTRAINT `fk_user_role_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `todolist`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_user_role_role1`
    FOREIGN KEY (`role_id`)
    REFERENCES `todolist`.`role` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `todolist`.`todo_list`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todolist`.`todo_list` (
                                                      `id` INT NOT NULL AUTO_INCREMENT,
                                                      `name` VARCHAR(45) NOT NULL,
    `created` DATETIME NOT NULL,
    `user_id` INT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
    INDEX `fk_list_user1_idx` (`user_id` ASC) VISIBLE,
    CONSTRAINT `fk_list_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `todolist`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `todolist`.`item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todolist`.`item` (
                                                 `id` INT NOT NULL AUTO_INCREMENT,
                                                 `text` TINYTEXT NOT NULL,
                                                 `state` TINYINT(1) NOT NULL,
    `created` DATETIME NOT NULL,
    `expired` DATETIME NULL,
    `todo_list_id` INT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_item_todo_list1_idx` (`todo_list_id` ASC) VISIBLE,
    CONSTRAINT `fk_item_todo_list1`
    FOREIGN KEY (`todo_list_id`)
    REFERENCES `todolist`.`todo_list` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `todolist`.`profile_picture`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todolist`.`profile_picture` (
                                                            `id` INT NOT NULL AUTO_INCREMENT,
                                                            `name` VARCHAR(64) NOT NULL,
    `url` VARCHAR(255) NOT NULL,
    `user_id` INT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_avatar_user1_idx` (`user_id` ASC) VISIBLE,
    UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
    UNIQUE INDEX `url_UNIQUE` (`url` ASC) VISIBLE,
    CONSTRAINT `fk_avatar_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `todolist`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
