
package com.example;

import java.sql.*;
import java.util.Scanner;

public class OrderManagement {

    static Scanner sc = new Scanner(System.in);

    static final String URL = "jdbc:mysql://localhost:3306/ecommerce_db";
    static final String USER = "root";
    static final String PASSWORD = "Amjad@1944";

    public static void main(String[] args) {

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {

            System.out.println("Database Connected Successfully!");

            int choice;

            do {
                System.out.println("\n===== ORDER MANAGEMENT SYSTEM =====");
                System.out.println("1. Insert Three Orders");
                System.out.println("2. Search Orders By Status");
                System.out.println("3. Increase Order Quantity");
                System.out.println("4. Update Order Status");
                System.out.println("5. Display Total Order Value");
                System.out.println("6. Delete Cancelled Order");
                System.out.println("0. Exit");

                System.out.print("Enter your choice: ");
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:
                        insertOrders(con);
                        break;

                    case 2:
                        searchOrders(con);
                        break;

                    case 3:
                        increaseQuantity(con);
                        break;

                    case 4:
                        updateStatus(con);
                        break;

                    case 5:
                        displayTotalValue(con);
                        break;

                    case 6:
                        deleteOrder(con);
                        break;

                    case 0:
                        System.out.println("Exiting...");
                        break;

                    default:
                        System.out.println("Invalid Choice!");
                }

            } while (choice != 0);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // (a) Insert Three Orders

    static void insertOrders(Connection con) {

        String sql = "INSERT INTO Orders VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 1; i <= 3; i++) {

                System.out.println("\nEnter Order " + i + " Details");

                System.out.print("Order ID: ");
                int id = Integer.parseInt(sc.nextLine());

                System.out.print("Customer Name: ");
                String customer = sc.nextLine();

                System.out.print("Product Name: ");
                String product = sc.nextLine();

                System.out.print("Quantity: ");
                int quantity = Integer.parseInt(sc.nextLine());

                System.out.print("Unit Price: ");
                double price = Double.parseDouble(sc.nextLine());

                System.out.print("Status: ");
                String status = sc.nextLine();

                ps.setInt(1, id);
                ps.setString(2, customer);
                ps.setString(3, product);
                ps.setInt(4, quantity);
                ps.setDouble(5, price);
                ps.setString(6, status);

                try {
                    ps.executeUpdate();
                    System.out.println("Order Inserted Successfully!");

                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Duplicate Order ID! Order not inserted.");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (b) Search Orders By Status

    static void searchOrders(Connection con) {

        String sql = "SELECT * FROM Orders WHERE status = ? AND quantity > 2 ORDER BY unit_price DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Order Status: ");
            String status = sc.nextLine();

            ps.setString(1, status);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println("\nOrder ID: " + rs.getInt("order_id"));
                System.out.println("Customer: " + rs.getString("customer_name"));
                System.out.println("Product: " + rs.getString("product_name"));
                System.out.println("Quantity: " + rs.getInt("quantity"));
                System.out.println("Unit Price: " + rs.getDouble("unit_price"));
                System.out.println("Status: " + rs.getString("status"));
            }

            if (!found) {
                System.out.println("No matching orders found!");
            }

            rs.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (c) Increase Existing Order Quantity

    static void increaseQuantity(Connection con) {

        String sql = "UPDATE Orders SET quantity = quantity + ? WHERE order_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Order ID: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Additional Quantity: ");
            int quantity = Integer.parseInt(sc.nextLine());

            ps.setInt(1, quantity);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Quantity Updated Successfully!");
            } else {
                System.out.println("Order Does Not Exist!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (d) Update Order Status

    static void updateStatus(Connection con) {

        try {

            System.out.print("Enter Order ID: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("Enter New Status (Pending/Shipped/Delivered): ");
            String status = sc.nextLine();

            if (!status.equalsIgnoreCase("Pending") &&
                !status.equalsIgnoreCase("Shipped") &&
                !status.equalsIgnoreCase("Delivered")) {

                System.out.println("Invalid Status!");
                return;
            }

            String check = "SELECT * FROM Orders WHERE order_id = ?";

            try (PreparedStatement ps = con.prepareStatement(check)) {

                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("Order Does Not Exist!");
                    return;
                }

                rs.close();
            }

            String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, status);
                ps.setInt(2, id);

                ps.executeUpdate();

                System.out.println("Status Updated Successfully!");

                displayOrder(con, id);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void displayOrder(Connection con, int id) {

        String sql = "SELECT * FROM Orders WHERE order_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                System.out.println("\nUpdated Order Details");

                System.out.println("Order ID: " + rs.getInt("order_id"));
                System.out.println("Customer: " + rs.getString("customer_name"));
                System.out.println("Product: " + rs.getString("product_name"));
                System.out.println("Quantity: " + rs.getInt("quantity"));
                System.out.println("Unit Price: " + rs.getDouble("unit_price"));
                System.out.println("Status: " + rs.getString("status"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // (e) Display Total Order Value

    static void displayTotalValue(Connection con) {

        String sql = "SELECT SUM(quantity * unit_price) AS total FROM Orders";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("Total Order Value: " + rs.getDouble("total"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Delete Order Only If Status Is Cancelled

    static void deleteOrder(Connection con) {

        String sql = "DELETE FROM Orders WHERE order_id = ? AND status = 'Cancelled'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter Order ID to Delete: ");
            int id = Integer.parseInt(sc.nextLine());

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Cancelled Order Deleted Successfully!");
            } else {
                System.out.println("Order Not Found or Status Is Not Cancelled!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
