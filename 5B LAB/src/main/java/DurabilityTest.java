package src.main.java;
import javax.persistence.*;
import java.util.List;

public class DurabilityTest {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");

    public static void main(String[] args) {
        // Case 1: Successful transaction
        System.out.println("=== Case 1: Successful Transaction ===");
        showOrders();
        addOrder(3L, 103L, 5); // Add a valid order
        showOrders();

        // Case 2: Transaction failure
        System.out.println("\n=== Case 2: Failed Transaction ===");
        showOrders();
        addOrderWithError(4L, null, 10); // Intentionally cause an error
        showOrders();
    }

    private static void addOrder(Long customerId, Long productId, int quantity) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Order order = new Order();
            order.setCustomerId(customerId);
            order.setProductId(productId);
            order.setQuantity(quantity);

            em.persist(order);
            tx.commit();
            System.out.println("Transaction committed: Order added successfully.");
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Transaction rolled back: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private static void addOrderWithError(Long customerId, Long productId, int quantity) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Order order = new Order();
            order.setCustomerId(customerId);
            order.setProductId(productId); // productId is null to simulate an error
            order.setQuantity(quantity);

            em.persist(order);
            tx.commit();
            System.out.println("Transaction committed: Order added successfully.");
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Transaction rolled back: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private static void showOrders() {
        EntityManager em = emf.createEntityManager();
        List<Order> orders = em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
        System.out.println("Orders in the database:");
        for (Order order : orders) {
            System.out.println(order);
        }
        em.close();
    }
}
