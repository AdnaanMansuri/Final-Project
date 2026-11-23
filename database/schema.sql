-- Last-Minute Food Rescue Network - MySQL 8 schema + seed data
-- Loaded automatically by docker-compose on first start.

CREATE DATABASE IF NOT EXISTS food_rescue;
USE food_rescue;

CREATE TABLE enterprise (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    type        ENUM('SUPPLIER','CHARITY') NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE organization (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    enterprise_id  INT NOT NULL,
    name           VARCHAR(100) NOT NULL,
    type           ENUM('BAKERY','GROCERY_STORE','FOOD_BANK','SHELTER') NOT NULL,
    address        VARCHAR(200),
    FOREIGN KEY (enterprise_id) REFERENCES enterprise(id) ON DELETE CASCADE
);

CREATE TABLE user_account (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    username         VARCHAR(50)  NOT NULL UNIQUE,
    password_hash    VARCHAR(64)  NOT NULL,          -- SHA-256 hex
    full_name        VARCHAR(100) NOT NULL,
    email            VARCHAR(100),
    role             ENUM('SYSTEM_ADMIN','SUPPLIER_MANAGER','STORE_STAFF',
                          'CHARITY_MANAGER','CHARITY_COORDINATOR') NOT NULL,
    organization_id  INT NULL,                       -- NULL only for SYSTEM_ADMIN
    FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE
);

CREATE TABLE food_item (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    supplier_org_id  INT NOT NULL,
    posted_by        INT NOT NULL,
    name             VARCHAR(100) NOT NULL,
    category         ENUM('BAKERY','PRODUCE','DAIRY','PREPARED_MEAL','PACKAGED') NOT NULL,
    quantity         DECIMAL(8,2) NOT NULL CHECK (quantity > 0),
    unit             VARCHAR(20) NOT NULL,           -- kg, loaves, boxes...
    allergens        VARCHAR(200),
    pickup_deadline  DATETIME NOT NULL,
    status           ENUM('PENDING_APPROVAL','AVAILABLE','CLAIMED',
                          'PICKED_UP','EXPIRED','REJECTED') NOT NULL DEFAULT 'PENDING_APPROVAL',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_org_id) REFERENCES organization(id) ON DELETE CASCADE,
    FOREIGN KEY (posted_by)       REFERENCES user_account(id) ON DELETE CASCADE
);

CREATE TABLE food_need (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    charity_org_id  INT NOT NULL,
    category        ENUM('BAKERY','PRODUCE','DAIRY','PREPARED_MEAL','PACKAGED') NOT NULL,
    quantity        DECIMAL(8,2) NOT NULL CHECK (quantity > 0),
    description     VARCHAR(300),
    needed_by       DATE NOT NULL,
    FOREIGN KEY (charity_org_id) REFERENCES organization(id) ON DELETE CASCADE
);

-- One table for all 4 work-request types (single-table inheritance).
CREATE TABLE work_request (
    id                    INT AUTO_INCREMENT PRIMARY KEY,
    type                  ENUM('DONATION_LISTING','CLAIM_APPROVAL',
                               'FOOD_CLAIM','FOOD_NEED') NOT NULL,
    sender_id             INT NOT NULL,
    receiver_id           INT NULL,                  -- NULL = any admin of target enterprise
    target_enterprise_id  INT NULL,
    food_item_id          INT NULL,
    food_need_id          INT NULL,
    charity_org_id        INT NULL,
    pickup_time           DATETIME NULL,
    status                ENUM('PENDING','ACCEPTED','REJECTED','COMPLETED','EXPIRED')
                          NOT NULL DEFAULT 'PENDING',
    message               VARCHAR(300),
    request_date          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolve_date          DATETIME NULL,
    FOREIGN KEY (sender_id)            REFERENCES user_account(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id)          REFERENCES user_account(id) ON DELETE SET NULL,
    FOREIGN KEY (target_enterprise_id) REFERENCES enterprise(id)   ON DELETE CASCADE,
    FOREIGN KEY (food_item_id)         REFERENCES food_item(id)    ON DELETE CASCADE,
    FOREIGN KEY (food_need_id)         REFERENCES food_need(id)    ON DELETE CASCADE,
    FOREIGN KEY (charity_org_id)       REFERENCES organization(id) ON DELETE CASCADE
);

-- ---------- Seed data ----------
INSERT INTO enterprise (name, type) VALUES
  ('Boston Food Suppliers', 'SUPPLIER'),
  ('Boston Community Charities', 'CHARITY');

INSERT INTO organization (enterprise_id, name, type, address) VALUES
  (1, 'Sunrise Bakery',      'BAKERY',        '12 Tremont St, Boston'),
  (1, 'FreshMart Grocery',   'GROCERY_STORE', '88 Huntington Ave, Boston'),
  (2, 'Greater Boston Food Bank', 'FOOD_BANK', '70 Southampton St, Boston'),
  (2, 'Hope Shelter',        'SHELTER',       '5 Mass Ave, Boston');

-- All seed passwords are "password123" (SHA-256)
SET @pw = SHA2('password123', 256);
INSERT INTO user_account (username, password_hash, full_name, email, role, organization_id) VALUES
  ('admin',     @pw, 'System Administrator', 'admin@foodrescue.org',   'SYSTEM_ADMIN',        NULL),
  ('smanager',  @pw, 'Sam Baker',            'sam@sunrise.com',        'SUPPLIER_MANAGER',    1),
  ('staff1',    @pw, 'Lee Chen',             'lee@sunrise.com',        'STORE_STAFF',         1),
  ('cmanager',  @pw, 'Priya Shah',           'priya@gbfb.org',         'CHARITY_MANAGER',     3),
  ('coord1',    @pw, 'Jordan Ellis',         'jordan@gbfb.org',        'CHARITY_COORDINATOR', 3);
