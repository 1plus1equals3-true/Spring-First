# Spring-First

```
CREATE DATABASE spring;
GRANT ALL PRIVILEGES ON spring.* TO user@localhost;
```

member

```
CREATE TABLE `member` (
	`idx` INT NOT NULL AUTO_INCREMENT,
	`userid` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`pwd1` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`pwd2` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`name` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`gender` CHAR(1) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`birth` DATE NULL DEFAULT NULL,
	`hobby` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`regdate` DATETIME NULL DEFAULT NULL,
	`member_rank` INT NULL DEFAULT '1',
	`originalfile` VARCHAR(200) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`dir` VARCHAR(200) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
	PRIMARY KEY (`idx`) USING BTREE,
	UNIQUE INDEX `uid` (`userid`) USING BTREE
)
COLLATE='utf8mb4_0900_ai_ci'
ENGINE=InnoDB
AUTO_INCREMENT=18
;
```

jdk

```
openjdk-17.0.2_windows-x64_bin
```
