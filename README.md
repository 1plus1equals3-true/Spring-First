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
	`pwd` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
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
AUTO_INCREMENT=25
;

```

board

```
CREATE TABLE `board` (
	`idx` INT NOT NULL AUTO_INCREMENT,
	`userid` VARCHAR(50) NULL DEFAULT NULL COMMENT 'FK' COLLATE 'utf8mb4_0900_ai_ci',
	`name` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`pwd` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`title` VARCHAR(1000) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`content` TEXT NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`hit` INT NULL DEFAULT '0',
	`regdate` DATETIME NOT NULL,
	`ip` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`boardtype` INT NULL DEFAULT '2',
	PRIMARY KEY (`idx`) USING BTREE
)
COLLATE='utf8mb4_0900_ai_ci'
ENGINE=InnoDB
AUTO_INCREMENT=6
;

```

board_attachments

```
CREATE TABLE `board_attachments` (
	`idx` INT NOT NULL AUTO_INCREMENT,
	`bidx` INT NOT NULL,
	`originalfile` VARCHAR(200) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`dir` VARCHAR(200) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
	PRIMARY KEY (`idx`) USING BTREE
)
COLLATE='utf8mb4_0900_ai_ci'
ENGINE=InnoDB
AUTO_INCREMENT=6
;

```

board_comments

```
CREATE TABLE `board_comments` (
	`idx` INT NOT NULL AUTO_INCREMENT,
	`bidx` INT NOT NULL,
	`uid` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`name` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`pwd` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`ment` TEXT NOT NULL COLLATE 'utf8mb4_0900_ai_ci',
	`regdate` DATETIME NOT NULL,
	PRIMARY KEY (`idx`) USING BTREE
)
COLLATE='utf8mb4_0900_ai_ci'
ENGINE=InnoDB
;

```

jdk

```
openjdk-17.0.2_windows-x64_bin
```
