DROP TABLE IF EXISTS DECODE CASCADE;
CREATE TABLE DECODE(
	CODE		VARCHAR(50),
	CODE_TYPE	VARCHAR(50) NOT NULL,
	DESCRIPTION	VARCHAR(200),	
	CREATE_DATE	TIMESTAMP,
	UPDATE_DATE	TIMESTAMP,
	PRIMARY KEY (CODE)
);


DROP TABLE IF EXISTS ACCOUNT CASCADE;
CREATE TABLE ACCOUNT(
	ID		BIGSERIAL,
	ACC_ID		VARCHAR(100) NOT NULL,
	ACC_NAME	TEXT,
	STATUS		BOOLEAN,
  ACC_TYPE VARCHAR(50),
	CREATE_DATE	TIMESTAMP,
	UPDATE_DATE	TIMESTAMP,
	PRIMARY KEY (ID),
	UNIQUE (ACC_ID),
  FOREIGN KEY (ACC_TYPE) REFERENCES DECODE(CODE)
);

/* Asset table consists of all the possible assets in market*/
DROP TABLE IF EXISTS ASSET CASCADE;
CREATE TABLE ASSET (
        ID      BIGSERIAL,
        SYMBOL  VARCHAR(15) NOT NULL,
        DESCRIPTION TEXT,
	FIGI VARCHAR(100),
	MIC VARCHAR(50),
	CURRENCY VARCHAR(10),
	TYPE VARCHAR(100),
        CREATE_DATE TIMESTAMP,
        UPDATE_DATE TIMESTAMP,
        PRIMARY KEY (ID)
);

/*Positions table describes the market value of each asset held by the account*/
DROP TABLE IF EXISTS POSITION CASCADE;
CREATE TABLE POSITION (
  	ID BIGSERIAL,
	ACC_ID		BIGINT,
	ASSET_ID 	BIGINT,
	DESCRIPTION    	TEXT,
	MARKET_VALUE 	FLOAT,
	CREATE_DATE 	TIMESTAMP,
	UPDATE_DATE		TIMESTAMP,
	PRIMARY KEY (ID),
	FOREIGN KEY (ASSET_ID) REFERENCES ASSET(ID),
	FOREIGN KEY (ACC_ID) REFERENCES ACCOUNT(ID)
);

/* Positions_lot table shows how many assets were bought in single transaction */
DROP TABLE IF EXISTS POSITION_LOT CASCADE;
CREATE TABLE POSITION_LOT (
	ID          BIGSERIAL,
	POSITION_ID BIGINT,
	DESCRIPTION TEXT,
	QUANTITY 	INT,
	MARKET_VALUE FLOAT,
	CREATE_DATE TIMESTAMP,
	UPDATE_DATE TIMESTAMP,
  	PRIMARY KEY (ID),
	FOREIGN KEY (POSITION_ID) REFERENCES POSITION(ID)
);

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
  id bigserial,
  first_name character varying(45) NOT NULL,
  last_name character varying(45) NOT NULL,
  middle_initial character varying(1),
  status character varying(45),
  email character varying(90) NOT NULL,
  username character varying(45) NOT NULL,
  created_date timestamp without time zone,
  modified_date timestamp without time zone,
  PRIMARY KEY (id),
  UNIQUE(username),
  UNIQUE(email)
);

DROP TABLE IF EXISTS role;
CREATE TABLE role
( 
  id serial,
  name character varying(45) UNIQUE NOT NULL,
  description character varying(180) NOT NULL,
  created_date timestamp without time zone,
  modified_date timestamp without time zone,
  PRIMARY KEY (ID)
);

DROP TABLE IF EXISTS user_role_map;
CREATE TABLE user_role_map
( 
  id bigserial,
  user_id bigint,
  role_id int,
  status boolean not null,
  created_date timestamp without time zone,
  modified_date timestamp without time zone,
  PRIMARY KEY (ID),
  FOREIGN KEY (user_id) REFERENCES users(ID),
  FOREIGN KEY (role_id) REFERENCES role(ID),
  UNIQUE(user_id,role_id)
);

DROP TABLE IF EXISTS user_account_map;
CREATE TABLE user_account_map
( 
  id bigserial,
  user_id bigint,
  acc_id bigint,
  status boolean,
  created_date timestamp without time zone,
  modified_date timestamp without time zone,
  PRIMARY KEY (ID),
  FOREIGN KEY (user_id) REFERENCES users(ID),
  FOREIGN KEY (acc_id) REFERENCES account(ID),
  UNIQUE(user_id,acc_id)
);



insert into users(first_name,last_name,middle_initial,status,email,username,created_date,modified_date)
values
('Pratul','Patwari','','active','pratul.patwari@gmail.com','pratul',NOW(),NOW());

insert into role(description,created_date,modified_date,name)
values
('Technical',NOW(),NOW(),'TECH');

insert into role(description,created_date,modified_date,name)
values
('Business',NOW(),NOW(),'BUSINESS');

insert into role(description,created_date,modified_date,name)
values
('Engineer',NOW(),NOW(),'ENG');


insert into user_role_map(user_id,role_id,status,created_date,modified_date)
values
(1,(select id from role where name='TECH'),true,NOW(),NOW());

INSERT INTO ASSET(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('JPM','JP Morgan Chase',102.78,NOW(),NOW());

INSERT INTO ASSET(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('C','Citigroup Inc.',47.41,NOW(),NOW());

INSERT INTO ASSET(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('SAVE','Spirit Airlines',14.08,NOW(),NOW());

INSERT INTO ASSET(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('ICD','Independent Contract Drilling, Inc.',6.25,NOW(),NOW());

INSERT INTO ASSET(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('UAL','United Airlines',31.50,NOW(),NOW());

INSERT INTO ASSET(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('AAL','American Airlines',12.51,NOW(),NOW());

/**************************************************************************/
INSERT INTO DECODE (CODE,CODE_TYPE,DESCRIPTION,CREATE_DATE,UPDATE_DATE)
VALUES('DOM', 'ACCOUNT', 'Account type', NOW(), NOW());

INSERT INTO DECODE (CODE,CODE_TYPE,DESCRIPTION,CREATE_DATE,UPDATE_DATE)
VALUES('GLB', 'ACCOUNT', 'Global Account type', NOW(), NOW());

INSERT INTO ACCOUNT(ACC_ID,ACC_NAME,STATUS,ACC_TYPE,CREATE_DATE,UPDATE_DATE)
VALUES
('324213','Domestic account',TRUE,'DOM',NOW(),NOW());

INSERT INTO ACCOUNT(ACC_ID,ACC_NAME,STATUS,ACC_TYPE,CREATE_DATE,UPDATE_DATE)
VALUES
('324214','Domestic account',TRUE,'DOM',NOW(),NOW());

INSERT INTO ACCOUNT(ACC_ID,ACC_NAME,STATUS,ACC_TYPE,CREATE_DATE,UPDATE_DATE)
VALUES
('324215','Domestic account',TRUE,'DOM',NOW(),NOW());

INSERT INTO ACCOUNT(ACC_ID,ACC_NAME,STATUS,ACC_TYPE,CREATE_DATE,UPDATE_DATE)
VALUES
('324216','Domestic account',TRUE,'DOM',NOW(),NOW());

insert into user_account_map(user_id,acc_id,status,created_date,modified_date)
values
(1,1,TRUE,NOW(),NOW());

/********************************************************/

DROP TABLE IF EXISTS TRANSACTIONS CASCADE;
CREATE TABLE TRANSACTIONS(
	ID		BIGINT,
	CODE	VARCHAR(50),
	AMOUNT 	FLOAT,
	FROM_ACCOUNT BIGINT NOT NULL,
	TO_ACCOUNT VARCHAR(100),
	IS_SCHEDULED BOOLEAN DEFAULT FALSE,
	STATUS 		VARCHAR(50) NOT NULL,
	TRANSACTION_DATE	DATE NOT NULL,
	DESCRIPTION	VARCHAR(200),	
	CREATE_DATE	TIMESTAMP,
	UPDATE_DATE	TIMESTAMP,
	PRIMARY KEY (ID),
	FOREIGN KEY (FROM_ACCOUNT) REFERENCES ACCOUNT(ID),
	FOREIGN KEY (CODE) REFERENCES DECODE(CODE)
);

DROP TABLE IF EXISTS TRANSACTION_HISTORY CASCADE;
CREATE TABLE TRANSACTION_HISTORY(
	ID		BIGINT,
	TRANSACTION_ID	BIGINT,
	PARENT_ID BIGINT,
	STATUS 		VARCHAR(50) NOT NULL,
	TRANSACTION_DATE	DATE NOT NULL,
	MODIFIED_BY BIGINT NOT NULL,
	CREATE_DATE	TIMESTAMP,
	UPDATE_DATE	TIMESTAMP,
	PRIMARY KEY (ID),
	FOREIGN KEY (TRANSACTION_ID) REFERENCES TRANSACTIONS(ID),
	FOREIGN KEY (PARENT_ID) REFERENCES TRANSACTIONS(ID),
	FOREIGN KEY (MODIFIED_BY) REFERENCES USERS(ID),
	UNIQUE(TRANSACTION_ID,PARENT_ID)
);