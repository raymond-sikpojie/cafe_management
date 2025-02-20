SELECT * FROM defaultdb.users u ;

SELECT * FROM defaultdb.users u WHERE u.email = 'omonray@yahoo.com';

UPDATE products p SET status = 'false' WHERE p.id = '2'


DROP TABLE users;

SELECT * FROM defaultdb.categories c;

SELECT * FROM defaultdb.products p;

SELECT * FROM defaultdb.bills b;

TRUNCATE bills; 

SELECT * FROM categories c INNER JOIN products p on p.category = c.id WHERE p.status = 'true'

DELETE FROM products p WHERE p.id = '13';



