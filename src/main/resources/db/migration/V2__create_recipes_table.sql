CREATE TABLE TB_RECIPES (
                            ID           BIGSERIAL         NOT NULL,
                            UNIQUE_ID    VARCHAR(255)      NOT NULL,
                            TITLE        VARCHAR(200)      NOT NULL,
                            DESCRIPTION  TEXT,
                            IMAGE_URL    VARCHAR(500),
                            IS_PRIVATE   BOOLEAN           DEFAULT FALSE NOT NULL,
                            USER_ID      BIGINT            NOT NULL,
                            CREATED_AT   TIMESTAMP         DEFAULT CURRENT_TIMESTAMP NOT NULL,
                            UPDATED_AT   TIMESTAMP,
                            CONSTRAINT PK_TB_RECIPES PRIMARY KEY (ID),
                            CONSTRAINT UK_TB_RECIPES_UID UNIQUE (UNIQUE_ID),
                            CONSTRAINT FK_TB_RECIPES_USER
                                FOREIGN KEY (USER_ID)
                                    REFERENCES TB_USERS (ID)
);