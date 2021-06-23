The application starts by requesting the customer ID. 
If the given value is -1, the user is prompted with all the necessary information to create a new customer. 
The main menu of the program is as follows: 
P (Products): Lists all products in products table 
O (Order): Orders a product given a product id and quantity. Each customer can only order each product once.
R (Return): Given a product id, returns that product to the shop with a quantity of 1. 
Program ensures that the customer has ordered this product before and that the quantity ordered in the sales table will not drop below 0 due to return.
S (Product Search): Searches the products table given a substring of the product name.
E (Expenditure): Lists all orders the current customer has made. 
C (Current Budget): Lists the budget for the current customer. 
X (Exit): Exit application.
