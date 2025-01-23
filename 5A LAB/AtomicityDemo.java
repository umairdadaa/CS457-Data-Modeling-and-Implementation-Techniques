import java.sql.*;

public class AtomicityDemo {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            // Establish connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/5A", "root", "12Umair@");
            conn.setAutoCommit(false); // Disable auto-commit for transaction management

            // Retrieve account balances
            PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM accounts WHERE id = ?");
            stmt.setInt(1, 1);
            ResultSet rs1 = stmt.executeQuery();
            rs1.next();
            int balance1 = rs1.getInt("balance");
            rs1.close();

            stmt.setInt(1, 2);
            ResultSet rs2 = stmt.executeQuery();
            rs2.next();
            int balance2 = rs2.getInt("balance");
            rs2.close();

            int transferAmount = 300; // Amount to transfer
            int minBalance = 50;      // Minimum balance that must be maintained

            // Case (a): Successful transfer
            if (balance1 >= transferAmount + minBalance) {
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE accounts SET balance = ? WHERE id = ?");
                updateStmt.setInt(1, balance1 - transferAmount);
                updateStmt.setInt(2, 1);
                updateStmt.executeUpdate();

                updateStmt.setInt(1, balance2 + transferAmount);
                updateStmt.setInt(2, 2);
                updateStmt.executeUpdate();

                conn.commit(); // Commit the transaction
                System.out.println("Money transferred successfully.");
            }
            // Case (b): Insufficient funds
            else if (balance1 < transferAmount) {
                System.out.println("Insufficient funds. Money not transferred.");
                conn.rollback(); // Rollback the transaction
            }
            // Case (c): Not maintaining the minimum balance
            else if (balance1 < transferAmount + minBalance) {
                System.out.println("Minimum balance requirement not met. Money not transferred.");
                conn.rollback(); // Rollback the transaction
            }

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback the transaction on error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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
