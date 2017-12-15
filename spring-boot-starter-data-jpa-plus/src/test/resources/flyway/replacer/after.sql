
CREATE TABLE IF NOT EXISTS "example"."article" (
  "id" BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  "title" VARCHAR(2147483647) NOT NULL COMMENT 'Title',
  "create_date" DATETIME NOT NULL COMMENT 'Create Date',
  "update_date" DATETIME NOT NULL COMMENT 'Update Date',
  PRIMARY KEY ("id"))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS "example"."article_image" (
  "id" BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  "article_id" BIGINT UNSIGNED NOT NULL COMMENT 'Article ID',
  "small_url" VARCHAR(512) NOT NULL COMMENT 'URL',
  "large_url" VARCHAR(512) NOT NULL COMMENT 'URL',
  "create_date" DATETIME NOT NULL COMMENT 'Create Date',
  "update_date" DATETIME NOT NULL COMMENT 'Update Date',
  PRIMARY KEY ("id"),
  INDEX "ARTICLE_IMAGE-ARTICLE_ID" ("article_id" ASC),
  CONSTRAINT "ARTICLE_IMAGE-ARTICLE_ID"
    FOREIGN KEY ("article_id")
    REFERENCES "example"."article" ("id")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

ALTER TABLE "example"."m_video" ADD COLUMN "thumb_url" VARCHAR(512) NOT NULL AFTER "article_id";
ALTER TABLE "example"."m_video" CHANGE COLUMN "thumb_url" "thumb_url" VARCHAR(2147483647) NOT NULL;
ALTER TABLE "example"."m_video" MODIFY COLUMN "thumb_url" VARCHAR(512) NOT NULL;
ALTER TABLE "example"."m_video" DROP CONSTRAINT "M_VIDEO-THUMB_URL";

