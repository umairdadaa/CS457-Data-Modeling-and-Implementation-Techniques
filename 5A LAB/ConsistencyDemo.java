import java.sql.*;

public class ConsistencyDemo {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/5A", "root", "12Umair@");

            // =======================
            // Transaction 1: Address Change
            // =======================
            try {
                conn.setAutoCommit(false); // Disable auto-commit for the first transaction
                System.out.println("Starting Transaction 1: Address Change");

                // Address change scenario
                PreparedStatement updateAddressStmt = conn.prepareStatement("UPDATE customers SET address = ? WHERE id = ?");
                updateAddressStmt.setString(1, "123 New Street"); // New address
                updateAddressStmt.setInt(2, 1); // Customer ID
                int rowsAffected = updateAddressStmt.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit(); // Commit the transaction if the address update is successful
                    System.out.println("Address change committed successfully. Database is consistent.");
                } else {
                    System.out.println("Address change failed. No matching customer found.");
                    conn.rollback(); // Rollback if no rows were updated
                }

            } catch (SQLException e) {
                System.out.println("Error in Transaction 1: " + e.getMessage());
                if (conn != null) conn.rollback();
            } finally {
                conn.setAutoCommit(true); // Reset auto-commit
                System.out.println("Transaction 1 completed.");
            }

// =======================
// Transaction 2: Product Purchase
// =======================
        try {
            conn.setAutoCommit(false); // Disable auto-commit for the second transaction
            System.out.println("Starting Transaction 2: Product Purchase");

            // Product purchase scenario
            int productId = 101; // Make sure this productId exists in your products table
            int requestedQuantity = 5; // Adjust this value based on your data

            // Check the available quantity of the product
            PreparedStatement quantityStmt = conn.prepareStatement("SELECT quantity FROM products WHERE id = ?");
            quantityStmt.setInt(1, productId);
            ResultSet rs = quantityStmt.executeQuery();

            if (rs.next()) {
                int availableQuantity = rs.getInt("quantity");
                System.out.println("Available Quantity: " + availableQuantity);

                if (availableQuantity >= requestedQuantity) {
                    // Case: Product can be purchased
                    PreparedStatement updateProductStmt = conn.prepareStatement("UPDATE products SET quantity = ? WHERE id = ?");
                    updateProductStmt.setInt(1, availableQuantity - requestedQuantity); // Deduct the requested quantity
                    updateProductStmt.setInt(2, productId);
                    int updateCount = updateProductStmt.executeUpdate();

                    if (updateCount > 0) {
                        conn.commit(); // Commit the transaction
                        System.out.println("Product purchased successfully. Database remains consistent.");
                    } else {
                        System.out.println("Product update failed.");
                        conn.rollback(); // Rollback if update fails
                    }
                    updateProductStmt.close();
                } else {
                    // Case: Insufficient quantity
                    System.out.println("Insufficient product quantity. Purchase cannot be completed.");
                    conn.rollback(); // Rollback the transaction
                }
            } else {
                System.out.println("Product not found. Purchase cannot be completed.");
                conn.rollback(); // Rollback if product doesn't exist
            }
            rs.close();
            quantityStmt.close();

        } catch (SQLException e) {
            System.out.println("Error in Transaction 2: " + e.getMessage());
            if (conn != null) conn.rollback();
        } finally {
            conn.setAutoCommit(true); // Reset auto-commit
            System.out.println("Transaction 2 completed.");
}


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close(); // Close the connection
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
