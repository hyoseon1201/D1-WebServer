USE d1game;

CREATE TABLE accounts (
    account_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE characters (
    character_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id   BIGINT       NOT NULL,
    name         VARCHAR(50)  NOT NULL,
    class_type   VARCHAR(30)  NOT NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_account_id (account_id)
);

CREATE TABLE character_stats (
    character_id     BIGINT PRIMARY KEY,
    level            INT      NOT NULL DEFAULT 1,
    xp               INT      NOT NULL DEFAULT 0,
    attribute_points INT      NOT NULL DEFAULT 0,
    skill_points     INT      NOT NULL DEFAULT 0,
    strength         INT      NOT NULL DEFAULT 0,
    intelligence     INT      NOT NULL DEFAULT 0,
    dexterity        INT      NOT NULL DEFAULT 0,
    luck             INT      NOT NULL DEFAULT 0,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE character_skills (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    character_id BIGINT       NOT NULL,
    skill_tag    VARCHAR(100) NOT NULL,
    skill_level  INT          NOT NULL DEFAULT 1,
    INDEX idx_character_id (character_id),
    UNIQUE KEY uq_character_skill (character_id, skill_tag)
);

CREATE TABLE character_skill_slots (
    character_id BIGINT      NOT NULL,
    slot_key     CHAR(1)     NOT NULL,
    skill_tag    VARCHAR(100) NOT NULL,
    PRIMARY KEY (character_id, slot_key)
);

CREATE TABLE inventory_items (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    character_id  BIGINT       NOT NULL,
    slot_index    INT          NOT NULL,
    item_asset_id VARCHAR(100) NOT NULL,
    quantity      INT          NOT NULL DEFAULT 1,
    INDEX idx_character_id (character_id),
    UNIQUE KEY uq_character_slot (character_id, slot_index)
);

CREATE TABLE equipped_items (
    character_id  BIGINT       NOT NULL,
    slot_type     VARCHAR(20)  NOT NULL,
    item_asset_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (character_id, slot_type)
);

CREATE TABLE character_quick_slots (
    character_id  BIGINT       NOT NULL,
    slot_key      INT          NOT NULL,
    item_asset_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (character_id, slot_key)
);
