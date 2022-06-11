DROP TABLE Users CASCADE;
DROP TABLE Orders CASCADE;
DROP TABLE Menu CASCADE;
DROP TABLE ItemStatus;

CREATE TABLE Users(
	login char(50) UNIQUE NOT NULL, 
	phoneNum char(16) UNIQUE, 
	password char(50) NOT NULL,
	favItems char(400),
	type char(8) NOT NULL,
	PRIMARY KEY(login));

CREATE TABLE Menu(
	itemName char(50) UNIQUE NOT NULL,
	type char(20) NOT NULL,
	price real NOT NULL,
	description char(400),
	imageURL char(256),
	PRIMARY KEY(itemName));

CREATE TABLE Orders(
	orderid serial UNIQUE NOT NULL,
	login char(50), 
	paid boolean,
	timeStampRecieved timestamp NOT NULL,
	total real NOT NULL,
	PRIMARY KEY(orderid)
	--added so that when users put in an order but update their login/login gets deleted, you can still find the order
	FOREIGN KEY(login) REFERENCES Users(login)
	ON UPDATE CASCADE
	ON DELETE CASCADE);

CREATE TABLE ItemStatus(
	orderid integer,
	itemName char(50), 
	lastUpdated timestamp NOT NULL,
	status char(20), 
	comments char(130), 
	PRIMARY KEY(orderid,itemName),
	--added so that when users delete/update order, itemstatus will also be updated
	FOREIGN KEY(orderid) REFERENCES Orders(orderid),
	ON UPDATE CASCADE
	ON DELETE CASCADE,
	--added so that when manager updates menu, itemstatus gets updates as well
	FOREIGN KEY(itemName) REFERENCES Menu(itemName)
	ON UPDATE CASCADE
	ON DELETE CASCADE);
