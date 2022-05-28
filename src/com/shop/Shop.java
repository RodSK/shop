package com.shop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Shop {

	private Scanner input = new Scanner(System.in);
	private ResultSet queryResult;
	private int id;
	
	/****************************************/
	/******** Prepared Statements ***********/
	/****************************************/
	private PreparedStatement selectCustomerRow;
	private PreparedStatement selectSalesRow;
	private PreparedStatement insertSalesRow;
	private PreparedStatement selectProductsRow;
	private PreparedStatement updateBudget;
	private PreparedStatement updateQuantity;
	private PreparedStatement selectProductsName;
	private PreparedStatement selectTotalCost;
	private PreparedStatement selectAllCustomers;
	private PreparedStatement insertCustomerRow;

	private void setPreparedStatements(Connection conn) {
		try {
			selectCustomerRow = conn.prepareStatement("SELECT * FROM Customers WHERE cid = ?");
			selectSalesRow = conn.prepareStatement("SELECT * FROM Sales WHERE cid= ? AND pid= ?");
			insertSalesRow = conn.prepareStatement("INSERT INTO Sales (pid, cid, quantity) VALUES (?, ?, ?)");
			selectProductsRow = conn.prepareStatement("SELECT * FROM Products WHERE pid= ?");
			updateBudget = conn.prepareStatement("UPDATE Customers SET budget=budget- ? WHERE cid= ?");
			updateQuantity = conn.prepareStatement("UPDATE Sales SET quantity=quantity-1 WHERE pid= ? AND cid= ?");
			selectProductsName = conn.prepareStatement("SELECT * FROM Products WHERE name LIKE ?");
			selectTotalCost = conn.prepareStatement(
					"SELECT P.pid AS pid, P.name AS name, P.price*S.quantity AS total FROM Products P, Sales S WHERE S.pid=P.pid AND S.cid= ?");
			selectAllCustomers = conn.prepareStatement("SELECT * FROM Customers");
			insertCustomerRow = conn.prepareStatement("INSERT INTO Customers (cid, name, budget) VALUES (?, ?, ?)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Connection connect() {
		Connection conn = null;
		
		// Connect To Database
		try {
			// Load Driver - jdbcDriver
			Class.forName("oracle.jdbc.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:***********", "*******", "*******");
			System.out.println("Connection Successful");
		} catch (SQLException e) {
			System.out.println("Connection ERROR");
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
		return conn;
	}

	private void displayMenu() {
		System.out.println("\n===== MAIN MENU =====");
		System.out.println("P - List all products.");
		System.out.println("O – Order a product.");
		System.out.println("R – Return a product.");
		System.out.println("S - Search a product by name.");
		System.out.println("E - List all orders.");
		System.out.println("C - Current budget.");
		System.out.println("X - Exit.");
		System.out.print("\nEnter Menu Option: ");
	}

	private boolean isCustomerVerified() {
		try {
			selectCustomerRow.setInt(1, id);
			queryResult = selectCustomerRow.executeQuery();
			
			if (queryResult.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	private void menuP() {
		try {
			selectProductsName.setString(1, "%");
			queryResult = selectProductsName.executeQuery();
			System.out.printf("%-5s | %-20s | %-10s%n", "ID", "NAME", "PRICE");
			
			while (queryResult.next()) {
				System.out.printf("%-5d | %-20s | %.2f%n", queryResult.getInt("pid"), queryResult.getString("name"),
						queryResult.getDouble("price"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void menuO() {
		System.out.print("Enter Product ID: ");
		int productId = input.nextInt();
		System.out.print("\nEnter Quantity: ");
		int productQuantity = input.nextInt();
		System.out.println();
		
		try {
			selectProductsRow.setInt(1, productId);
			queryResult = selectProductsRow.executeQuery();
			
			if (queryResult.next()) {
				int total = queryResult.getInt("price") * productQuantity;
				selectSalesRow.setInt(1, id);
				selectSalesRow.setInt(2, productId);
				queryResult = selectSalesRow.executeQuery();
				
				if (queryResult.next()) {
					System.out.println("Product id " + productId + " was already purchased.");
				} else {
					insertSalesRow.setInt(1, productId);
					insertSalesRow.setInt(2, id);
					insertSalesRow.setInt(3, productQuantity);
					queryResult = insertSalesRow.executeQuery();

					updateBudget.setInt(1, total);
					updateBudget.setInt(2, id);
					queryResult = updateBudget.executeQuery();
					System.out.println("Order is Completed.");
				}
			} else {
				System.out.println("Product id " + productId + " does NOT exist.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void menuR() {
		System.out.print("Enter Product ID: ");
		int productId = input.nextInt();
		System.out.println();

		try {
			selectSalesRow.setInt(1, id);
			selectSalesRow.setInt(2, productId);
			queryResult = selectSalesRow.executeQuery();
			
			if (queryResult.next()) {
				if (queryResult.getInt("quantity") == 0) {
					System.out.println("All products id " + productId + " were already returned.");
				} else {
					updateQuantity.setInt(1, productId);
					updateQuantity.setInt(2, id);
					queryResult = updateQuantity.executeQuery();
					System.out.println("Product id " + productId + " quantity 1 was returned.");
				}
			} else {
				System.out.println("Product id " + productId + " was NOT ordered.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void menuS() {
		System.out.println("Enter Product Full or Partial Name: ");
		String productName = input.next();
		System.out.println();

		try {
			selectProductsName.setString(1, "%" + productName + "%");
			queryResult = selectProductsName.executeQuery();
			System.out.printf("%-5s | %-20s | %-10s%n", "ID", "NAME", "PRICE");
			
			while (queryResult.next()) {
				System.out.printf("%-5d | %-20s | %.2f%n", queryResult.getInt("pid"), queryResult.getString("name"),
						queryResult.getDouble("price"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void menuE() {
		try {
			selectTotalCost.setInt(1, id);
			queryResult = selectTotalCost.executeQuery();
			System.out.printf("%-5s | %-20s | %-10s%n", "ID", "NAME", "TOTAL");
			
			while (queryResult.next()) {
				System.out.printf("%-5d | %-20s | %-10.2f%n", queryResult.getInt("pid"), queryResult.getString("name"),
						queryResult.getDouble("total"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void menuB() {
		try {
			selectCustomerRow.setInt(1, id);
			queryResult = selectCustomerRow.executeQuery();
			queryResult.next();
			System.out.printf("Budget is: %.2f%n", queryResult.getDouble("budget"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createCustomer() {
		System.out.println("Enter Name: ");
		String customerName = input.next();
		System.out.print("Enter Budget: ");
		double customerBudget = input.nextDouble();
		System.out.println();

		try {
			queryResult = selectAllCustomers.executeQuery();
			do {
				if (!queryResult.next()) {
					break;
				}
				id = queryResult.getInt("cid") + 1;
			} while (true);

			insertCustomerRow.setInt(1, id);
			insertCustomerRow.setString(2, customerName);
			insertCustomerRow.setDouble(3, customerBudget);
			queryResult = insertCustomerRow.executeQuery();

			System.out.println("===== New Customer Was Created. =====");
			System.out.println("Customer ID: " + id + " | Name: " + customerName + " | Budget: " + customerBudget + "\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Shop shop = new Shop();
		String menuOption = "";
		Connection conn = shop.connect();
		
		if (conn != null) {
			shop.setPreparedStatements(conn);
			do {
				System.out.print("Enter Customer ID: ");
				shop.id = shop.input.nextInt();
				
				if (shop.id == -1) {
					shop.createCustomer();
					break;
				}
			} while (!shop.isCustomerVerified());

			while (!menuOption.equalsIgnoreCase("x")) {
				shop.displayMenu();
				menuOption = Character.toString(shop.input.next().charAt(0));
				System.out.println();
				
				switch(menuOption) {
					case "p", "P": {
						shop.menuP();
						break;
					}
					case "o", "O": {
						shop.menuO();
						break;
					}
					case "r", "R": {
						shop.menuR();
						break;
					}
					case "s", "S": {
						shop.menuS();
						break;
					}
					case "e", "E": {
						shop.menuE();
						break;
					}
					case "b", "B": {
						shop.menuB();
						break;
					}
					default: {
						System.out.println("Invalid option");
					}
				}
			}

			try {
				System.out.println("Connection closed.");
				if (shop.queryResult != null) {
					shop.queryResult.close();
				}
				if (shop.selectCustomerRow != null) {
					shop.selectCustomerRow.close();
				}
				if (shop.selectSalesRow != null) {
					shop.selectSalesRow.close();
				}
				if (shop.selectProductsRow != null) {
					shop.selectProductsRow.close();
				}
				if (shop.updateBudget != null) {
					shop.updateBudget.close();
				}
				if (shop.updateQuantity != null) {
					shop.updateQuantity.close();
				}
				if (shop.selectProductsName != null) {
					shop.selectProductsName.close();
				}
				if (shop.selectTotalCost != null) {
					shop.selectTotalCost.close();
				}
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("Connection FAILED.");
		}
	}
}
