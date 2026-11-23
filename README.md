# Last-Minute Food Rescue Network

INFO 5100 - Lab 10 Independent Project. A Java Swing platform that connects bakeries and grocery
stores with nearby charities so surplus food is claimed and picked up before it goes to waste.

## Ecosystem

| Enterprise | Organizations | Roles |
|---|---|---|
| Food Supplier | Bakeries, Grocery Stores | Supplier Manager (admin), Store Staff |
| Charity / Community | Food Banks, Shelters | Charity Manager (admin), Charity Coordinator |
| Platform | - | System Admin |

## Work requests

| # | Request | From -> To | Scope |
|---|---|---|---|
| WR1 | Donation Listing | Store Staff -> Supplier Manager | intra-enterprise |
| WR2 | Claim Approval | Charity Coordinator -> Charity Manager | intra-enterprise |
| WR3 | Food Claim | Charity -> Supplier Manager | **inter-enterprise** |
| WR4 | Food Need | Charity Coordinator -> Supplier enterprise | **inter-enterprise** |

## Tech stack

Java 17+, Swing, Maven (NetBeans), MySQL 8 in Docker, JDBC (MySQL Connector/J), FlatLaf.

## Setup

1. Start the database (first run creates the tables and demo data):
   ```
   docker compose up -d
   ```
2. Open the folder in **NetBeans** (File > Open Project). It is a Maven project; dependencies download automatically.
3. Run `edu.neu.foodrescue.App`.

MySQL Workbench connection: `localhost:3306`, user `foodapp`, password `foodapp123`, schema `food_rescue`.

To reset the database to the demo data: `docker compose down -v` then `docker compose up -d`.

## Demo accounts (password: `password123`)

| Username | Role | Organization |
|---|---|---|
| admin | System Admin | - |
| smanager | Supplier Manager | Sunrise Bakery |
| staff1 | Store Staff | Sunrise Bakery |
| cmanager | Charity Manager | Greater Boston Food Bank |
| coord1 | Charity Coordinator | Greater Boston Food Bank |

## Project structure

```
src/main/java/edu/neu/foodrescue
  App.java              entry point
  model/                Enterprise hierarchy, Organization, UserAccount, FoodItem, FoodNeed, interfaces
  model/role/           abstract Role + 5 roles (each builds its own work area)
  model/workrequest/    abstract WorkRequest + 4 request types
  model/enums/          status / type enums
  dao/                  Dao<T> interface, BaseDao (JDBC helpers), one DAO per table
  service/              login and business rules
  ui/                   MainFrame (CardLayout), LoginPanel, role work areas
  util/                 DBConnection (singleton), PasswordUtil, Validator
database/schema.sql     tables + seed data
docs/diagrams/          use case + class diagrams (PlantUML)
```
