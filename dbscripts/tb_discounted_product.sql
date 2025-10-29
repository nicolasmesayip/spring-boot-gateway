CREATE TABLE IF NOT EXISTS tb_discounted_product (
      ID BIGINT NOT NULL AUTO_INCREMENT,
      DISCOUNT_ID BIGINT NOT NULL,
      PRODUCT_SLUG VARCHAR(255) NOT NULL,
      PRIMARY KEY (ID),
      INDEX idx_product_slug (PRODUCT_SLUG),
      INDEX idx_discount_id (DISCOUNT_ID),
      CONSTRAINT fk_discounted_product_discount
      FOREIGN KEY (DISCOUNT_ID) REFERENCES tb_discount (ID)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);