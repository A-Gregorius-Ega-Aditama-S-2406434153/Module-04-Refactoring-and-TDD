package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentTest {

    @Test
    void defaultStatusIsPending() {
        Payment payment = new Payment();

        assertEquals(Payment.STATUS_PENDING, payment.getStatus());
    }

    @Test
    void canSetAndGetPaymentProperties() {
        Order order = buildOrder("order-1");
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");

        Payment payment = new Payment();
        payment.setId("payment-1");
        payment.setOrder(order);
        payment.setMethod("Voucher Code");
        payment.setStatus(Payment.STATUS_SUCCESS);
        payment.setPaymentData(paymentData);

        assertEquals("payment-1", payment.getId());
        assertEquals("order-1", payment.getOrder().getId());
        assertEquals("Voucher Code", payment.getMethod());
        assertEquals(Payment.STATUS_SUCCESS, payment.getStatus());
        assertEquals("ESHOP1234ABC5678", payment.getPaymentData().get("voucherCode"));
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
