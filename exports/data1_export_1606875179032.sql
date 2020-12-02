USE data1;



DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
`price` INT(50),
`productname` VARCHAR(60),
`id` INT(50),
PRIMARY KEY (id)
);


DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
`city` VARCHAR(50),
`name` VARCHAR(60),
`id` INT(50),
`age` INT(50),
PRIMARY KEY (id)
);


