package src.main.java;
import java.sql.*;

public class IsolationACIDSimulation {
    private static Connection conn; // Global connection object

    public static void main(String[] args) {
        try {
            // Initialize the database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/5A", "root", "12Umair@");        } catch (SQLException e) {
            System.err.println("Error establishing database connection.");
            e.printStackTrace();
            return; // Exit if the connection fails
        }

        // Thread for Transaction 1
        Thread transaction1 = new Thread(() -> {
            processTransaction(1, 2, 3000); // Transfer $3000 from Account-1 to Account-2
        });

        // Thread for Transaction 2
        Thread transaction2 = new Thread(() -> {
            readAccountBalance(1); // Read balance of Account-1
        });

        transaction1.start();

        // Add a delay to ensure Transaction 1 starts first
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        transaction2.start();

        // Close the connection after threads complete
        try {
            transaction1.join();
            transaction2.join();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static synchronized void processTransaction(int fromAccountId, int toAccountId, int amount) {
        boolean autoCommitOriginal = true;
        try {
            // Backup original autocommit setting
            autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false); // Start transaction

            // Fetch balances
            PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM accounts WHERE id = ?");
            stmt.setInt(1, fromAccountId);
            ResultSet rs1 = stmt.executeQuery();
            rs1.next();
            int balanceFrom = rs1.getInt("balance");
            rs1.close();

            stmt.setInt(1, toAccountId);
            ResultSet rs2 = stmt.executeQuery();
            rs2.next();
            int balanceTo = rs2.getInt("balance");
            rs2.close();

            System.out.println("[Transaction 1] Initial Balance: Account-1 = $" + balanceFrom + ", Account-2 = $" + balanceTo);

            // Check if transfer is possible
            if (balanceFrom - amount >= 1000) {
                // Perform transfer
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE accounts SET balance = ? WHERE id = ?");
                updateStmt.setInt(1, balanceFrom - amount);
                updateStmt.setInt(2, fromAccountId);
                updateStmt.executeUpdate();

                System.out.println("[Transaction 1] Transferring $" + amount + " from Account-1 to Account-2...");

                // Simulate delay
                System.out.println("[Transaction 1] Waiting for 5 seconds...");
                Thread.sleep(5000);
            

                updateStmt.setInt(1, balanceTo + amount);
                updateStmt.setInt(2, toAccountId);
                updateStmt.executeUpdate();

                conn.commit(); // Commit transaction
                System.out.println("[Transaction 1] Transfer Successful!");
                System.out.println("[Transaction 1] Final Balance: Account-1 = $" + (balanceFrom - amount) + ", Account-2 = $" + (balanceTo + amount));
            } else {
                System.out.println("[Transaction 1] Transfer Failed: Insufficient funds or minimum balance requirement not met.");
                conn.rollback(); // Rollback transaction on failure
            }
        } catch (Exception e) {
            try {
                conn.rollback(); // Rollback in case of failure
                System.out.println("[Transaction 1] Transaction Rolled Back Due to Error.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(autoCommitOriginal); // Restore original autocommit setting
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static synchronized void readAccountBalance(int accountId) {
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); // Set isolation level
            conn.setAutoCommit(true); // Ensure autocommit is enabled for reading

            PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM accounts WHERE id = ?");
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int balance = rs.getInt("balance");
                System.out.println("[Transaction 2] Account-" + accountId + " Balance: $" + balance);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
