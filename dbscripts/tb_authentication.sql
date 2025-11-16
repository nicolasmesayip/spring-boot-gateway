CREATE TABLE IF NOT EXISTS tb_authentication (
    EMAIL_ADDRESS                VARCHAR(100)      NOT NULL,
    PASSWORD                     VARCHAR(255)      NOT NULL,
    FAILED_LOGIN_ATTEMPTS        INTEGER           NOT NULL,
    IS_ACCOUNT_LOCKED            TINYINT(1)        NOT NULL,
    PASSWORD_UPDATED_AT          DATETIME(6)       NOT NULL,
    LAST_LOGIN_AT                DATETIME(6)       NULL,
    REGISTERED_AT                DATETIME(6)       NOT NULL,
    PRIMARY KEY (EMAIL_ADDRESS)
);