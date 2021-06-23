CREATE TABLE Products(
        pid NUMBER(5) PRIMARY KEY,
        name VARCHAR2(20),
        price NUMBER(6,2)
);

CREATE TABLE Customers(
	cid NUMBER(5) PRIMARY KEY,
	name VARCHAR2(20),
	budget NUMBER(6,2)
);

CREATE TABLE Sales(
	pid NUMBER(5),
	cid NUMBER(5),
	quantity NUMBER,
	PRIMARY KEY (pid, cid),
	FOREIGN KEY (pid) REFERENCES Products(pid),
	FOREIGN KEY (cid) REFERENCES Customers(cid),
	CHECK (quantity >= 0)
);
