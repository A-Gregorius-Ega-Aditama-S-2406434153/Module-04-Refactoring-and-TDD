package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryPaymentRepositoryTest {

    private InMemoryPaymentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPaymentRepository();
    }

    @Test
    void saveAndFindByIdWorkCorrectly() {
        Payment payment = buildPayment("payment-1", "order-1", Payment.STATUS_PENDING);

        repository.save(payment);
        Payment found = repository.findById("payment-1");

        assertNotNull(found);
        assertEquals("payment-1", found.getId());
        assertEquals("order-1", found.getOrder().getId());
    }

    @Test
    void findByIdReturnsNullWhenMissing() {
        Payment found = repository.findById("missing-id");

        assertNull(found);
    }

    @Test
    void findAllReturnsEmptyWhenNoData() {
        List<Payment> payments = repository.findAll();

        assertTrue(payments.isEmpty());
    }

    @Test
    void findAllReturnsAllStoredPayments() {
        repository.save(buildPayment("payment-1", "order-1", Payment.STATUS_SUCCESS));
        repository.save(buildPayment("payment-2", "order-2", Payment.STATUS_REJECTED));

        List<Payment> payments = repository.findAll();

        assertEquals(2, payments.size());
        assertEquals("payment-1", payments.get(0).getId());
        assertEquals("payment-2", payments.get(1).getId());
    }

    @Test
    void saveWithSameIdReplacesExistingPayment() {
        repository.save(buildPayment("payment-1", "order-1", Payment.STATUS_PENDING));
        repository.save(buildPayment("payment-1", "order-2", Payment.STATUS_SUCCESS));

        Payment found = repository.findById("payment-1");
        List<Payment> payments = repository.findAll();

        assertNotNull(found);
        assertEquals("order-2", found.getOrder().getId());
        assertEquals(Payment.STATUS_SUCCESS, found.getStatus());
        assertEquals(1, payments.size());
    }

    private Payment buildPayment(String paymentId, String orderId, String status) {
        Order order = buildOrder(orderId);

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setOrder(order);
        payment.setMethod("Voucher Code");
        payment.setStatus(status);
        return payment;
    }

    private Order buildOrder(String orderId) {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sample");
        product.setProductQuantity(1);

        List<Product> products = new ArrayList<>();
        products.add(product);
        return new Order(orderId, products, 1708560000L, "Tester");
    }
}
