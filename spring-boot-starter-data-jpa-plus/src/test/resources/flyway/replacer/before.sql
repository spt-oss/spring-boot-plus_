SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- Table `example`.`article`
CREATE TABLE IF NOT EXISTS `example`.`article` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` TEXT NOT NULL COMMENT 'Title',
  `create_date` DATETIME NOT NULL COMMENT 'Create Date',
  `update_date` DATETIME NOT NULL COMMENT 'Update Date',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
COMMENT = 'Article';

-- Dumping data for table `article`
LOCK TABLES `article` WRITE;
-- insert data
UNLOCK TABLES;

-- Table `example`.`article_image`
CREATE TABLE IF NOT EXISTS `example`.`article_image` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `article_id` BIGINT UNSIGNED NOT NULL COMMENT 'Article ID',
  `small_url` VARCHAR(512) CHARACTER SET 'ascii' NOT NULL COMMENT 'URL',
  `large_url` VARCHAR(512) CHARACTER SET 'ascii' COLLATE 'ascii_general_ci' NOT NULL COMMENT 'URL',
  `create_date` DATETIME NOT NULL COMMENT 'Create Date',
  `update_date` DATETIME NOT NULL COMMENT 'Update Date',
  PRIMARY KEY (`id`),
  INDEX `ARTICLE_IMAGE-ARTICLE_ID` (`article_id` ASC),
  CONSTRAINT `ARTICLE_IMAGE-ARTICLE_ID`
    FOREIGN KEY (`article_id`)
    REFERENCES `example`.`article` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Article Image';

ALTER TABLE `example`.`m_video` ADD COLUMN `thumb_url` VARCHAR(512) NOT NULL AFTER `article_id`;
ALTER TABLE `example`.`m_video` CHANGE COLUMN `thumb_url` `thumb_url` TEXT NOT NULL AFTER `article_id`;
ALTER TABLE `example`.`m_video` MODIFY COLUMN `thumb_url` VARCHAR(512) NOT NULL AFTER `article_id`;
ALTER TABLE `example`.`m_video` DROP /* UNIQUE */ INDEX "M_VIDEO-THUMB_URL";

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
