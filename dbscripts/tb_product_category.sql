CREATE TABLE IF NOT EXISTS tb_product_category (
  ID           BIGINT       NOT NULL AUTO_INCREMENT,
  NAME         VARCHAR(50)  NOT NULL,
  SLUG         VARCHAR(50)  NOT NULL,
  DESCRIPTION  VARCHAR(255) NOT NULL,
  IS_ACTIVE    TINYINT(1)   NOT NULL,
  CREATED_AT   DATETIME(6)  NOT NULL,
  UPDATED_AT   DATETIME(6)  NOT NULL,
  PRIMARY KEY (ID),
  UNIQUE KEY `uq_tb_product_category_name` (NAME),
  UNIQUE KEY `uq_tb_product_category_slug` (SLUG),
  INDEX `idx_tb_product_category_slug` (SLUG)
);