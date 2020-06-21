DROP TABLE IF EXISTS ACCOUNTS CASCADE;
CREATE TABLE ACCOUNTS(
	ID			SERIAL,
	ACC_ID		VARCHAR(100) NOT NULL,
	ACC_NAME	TEXT,
	STATUS		BOOLEAN,
	CREATE_DATE	TIMESTAMP,
	UPDATE_DATE	TIMESTAMP,
	PRIMARY KEY (ID),
	UNIQUE (ACC_ID)
);

/* Asset table consists of all the possible assets in market*/
DROP TABLE IF EXISTS ASSETS CASCADE;
CREATE TABLE ASSETS (
  	ID         SERIAL,
	SYMBOL 		VARCHAR(15) NOT NULL,
	DESCRIPTION TEXT,
	MARKET_VALUE FLOAT DEFAULT 0,
	CREATE_DATE TIMESTAMP,
	UPDATE_DATE TIMESTAMP,
  	PRIMARY KEY (ID)
);

/*Positions table describes the market value of each asset held by the account*/
DROP TABLE IF EXISTS POSITIONS CASCADE;
CREATE TABLE POSITIONS (
  	ID SERIAL,
	ACC_ID			BIGINT,
	ASSET_ID 		BIGINT,
	DESCRIPTION    	TEXT,
	MARKET_VALUE 	FLOAT,
	CREATE_DATE 	TIMESTAMP,
	UPDATE_DATE		TIMESTAMP,
	PRIMARY KEY (ID),
	FOREIGN KEY (ASSET_ID) REFERENCES ASSETS(ID),
	FOREIGN KEY (ACC_ID) REFERENCES ACCOUNTS(ID)
);

/* Positions_lot table shows how many assets were bought in single transaction */
DROP TABLE IF EXISTS POSITIONS_LOT CASCADE;
CREATE TABLE POSITIONS_LOT (
	ID          SERIAL,
	POSITION_ID	BIGINT,
	DESCRIPTION TEXT,
	QUANTITY 	INT,
	MARKET_VALUE FLOAT,
	CREATE_DATE TIMESTAMP,
	UPDATE_DATE TIMESTAMP,
  	PRIMARY KEY (ID),
	FOREIGN KEY (POSITION_ID) REFERENCES POSITIONS(ID)
);

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
  id serial,
  first_name character varying(45) NOT NULL,
  last_name character varying(45) NOT NULL,
  middle_initial character varying(1),
  password character varying(90),
  status character varying(45),
  email character varying(90) NOT NULL,
  username character varying(45) NOT NULL,
  created_date timestamp without time zone,
  modified_date timestamp without time zone,
  CONSTRAINT users_pkey PRIMARY KEY (id),
  CONSTRAINT users_username UNIQUE(username),
  CONSTRAINT users_email UNIQUE(email)
);

DROP TABLE IF EXISTS roles;
CREATE TABLE roles
( 
  id serial,
  description character varying(180) NOT NULL,
  created_date timestamp without time zone,
  modified_date timestamp without time zone,
  name character varying(45) UNIQUE NOT NULL,
  PRIMARY KEY (ID)
);

DROP TABLE IF EXISTS users_role_map;
CREATE TABLE users_role_map
( 
  id serial,
  user_id bigint,
  role_id bigint,
  created_date timestamp without time zone,
  modified_date timestamp without time zone,
  PRIMARY KEY (ID),
  FOREIGN KEY (user_id) REFERENCES users(ID),
  FOREIGN KEY (role_id) REFERENCES roles(ID)
);

DROP TABLE IF EXISTS user_accounts_map;
CREATE TABLE user_accounts_map
( 
  id serial,
  user_id bigint,
  acc_id bigint,
  status boolean,
  created_date timestamp without time zone,
  modified_date timestamp without time zone,
  PRIMARY KEY (ID),
  FOREIGN KEY (user_id) REFERENCES users(ID),
  FOREIGN KEY (acc_id) REFERENCES accounts(ID)
);



insert into users(first_name,last_name,middle_initial,password,status,email,username,created_date,modified_date)
values
('Pratul','Patwari','','pratul','active','pratul.patwari@gmail.com','pratul',NOW(),NOW());

insert into roles(description,created_date,modified_date,name)
values
('Technical',NOW(),NOW(),'TECH');

insert into roles(description,created_date,modified_date,name)
values
('Business',NOW(),NOW(),'BUSINESS');

insert into roles(description,created_date,modified_date,name)
values
('Engineer',NOW(),NOW(),'ENG');

insert into users_role_map(user_id,role_id,created_date,modified_date)
values
(1,1,NOW(),NOW());


INSERT INTO ASSETS(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('JPM','JP Morgan Chase',102.78,NOW(),NOW());

INSERT INTO ASSETS(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('C','Citigroup Inc.',47.41,NOW(),NOW());

INSERT INTO ASSETS(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('SAVE','Spirit Airlines',14.08,NOW(),NOW());

INSERT INTO ASSETS(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('ICD','Independent Contract Drilling, Inc.',6.25,NOW(),NOW());

INSERT INTO ASSETS(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('UAL','United Airlines',31.50,NOW(),NOW());

INSERT INTO ASSETS(SYMBOL,DESCRIPTION,MARKET_VALUE,CREATE_DATE,UPDATE_DATE)
VALUES
('AAL','American Airlines',12.51,NOW(),NOW());

/**************************************************************************/


INSERT INTO ACCOUNTS(ACC_ID,ACC_NAME,STATUS,CREATE_DATE,UPDATE_DATE)
VALUES
('324213','Domestic account',TRUE,NOW(),NOW());

INSERT INTO ACCOUNTS(ACC_ID,ACC_NAME,STATUS,CREATE_DATE,UPDATE_DATE)
VALUES
('324214','Domestic account',TRUE,NOW(),NOW());

INSERT INTO ACCOUNTS(ACC_ID,ACC_NAME,STATUS,CREATE_DATE,UPDATE_DATE)
VALUES
('324215','Domestic account',TRUE,NOW(),NOW());

INSERT INTO ACCOUNTS(ACC_ID,ACC_NAME,STATUS,CREATE_DATE,UPDATE_DATE)
VALUES
('324216','Domestic account',TRUE,NOW(),NOW());

insert into user_accounts_map(user_id,acc_id,status,created_date,modified_date)
values
(1,1,TRUE,NOW(),NOW());
