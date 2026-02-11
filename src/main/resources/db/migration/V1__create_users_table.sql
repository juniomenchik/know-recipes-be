CREATE TABLE TB_USERS (
                          ID           BIGSERIAL        NOT NULL,
                          USERNAME     VARCHAR(50)      NOT NULL,
                          EMAIL        VARCHAR(100)     NOT NULL,
                          PASSWORD     VARCHAR(255)     NOT NULL,
                          CREATED_AT   TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          CONSTRAINT PK_TB_USERS PRIMARY KEY (ID),
                          CONSTRAINT UK_TB_USERS_USERNAME UNIQUE (USERNAME),
                          CONSTRAINT UK_TB_USERS_EMAIL UNIQUE (EMAIL)
);