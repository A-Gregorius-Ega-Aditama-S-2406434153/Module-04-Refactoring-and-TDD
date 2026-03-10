package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void addPaymentByVoucherWithValidCodeSetsSuccess() {
        Order order = buildOrder("order-1");
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = paymentService.addPayment(order, "Voucher Code", paymentData);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertNotNull(payment.getId());
        assertFalse(payment.getId().isBlank());
        assertEquals(Payment.STATUS_SUCCESS, payment.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
        assertEquals(Payment.STATUS_SUCCESS, paymentCaptor.getValue().getStatus());
    }

    @Test
    void addPaymentByVoucherWithInvalidCodeSetsRejected() {
        Order order = buildOrder("order-1");
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "INVALID");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = paymentService.addPayment(order, "Voucher Code", paymentData);

        assertEquals(Payment.STATUS_REJECTED, payment.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void addPaymentByCodWithCompleteDataSetsSuccess() {
        Order order = buildOrder("order-2");
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("address", "Jl. Margonda 100");
        paymentData.put("deliveryFee", "10000");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = paymentService.addPayment(order, "Cash on Delivery", paymentData);

        assertEquals(Payment.STATUS_SUCCESS, payment.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
    }

    @Test
    void addPaymentByCodWithMissingAddressSetsRejected() {
        Order order = buildOrder("order-2");
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("address", "");
        paymentData.put("deliveryFee", "10000");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = paymentService.addPayment(order, "Cash on Delivery", paymentData);

        assertEquals(Payment.STATUS_REJECTED, payment.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void setStatusToSuccessUpdatesOrderStatusToSuccess() {
        Order order = buildOrder("order-3");
        Payment payment = buildPayment("payment-1", order);

        Payment updated = paymentService.setStatus(payment, Payment.STATUS_SUCCESS);

        assertEquals(Payment.STATUS_SUCCESS, updated.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
    }

    @Test
    void setStatusToRejectedUpdatesOrderStatusToFailed() {
        Order order = buildOrder("order-4");
        Payment payment = buildPayment("payment-2", order);

        Payment updated = paymentService.setStatus(payment, Payment.STATUS_REJECTED);

        assertEquals(Payment.STATUS_REJECTED, updated.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void getPaymentDelegatesToRepository() {
        Payment payment = buildPayment("payment-3", buildOrder("order-5"));
        when(paymentRepository.findById("payment-3")).thenReturn(payment);

        Payment found = paymentService.getPayment("payment-3");

        assertNotNull(found);
        assertEquals("payment-3", found.getId());
        verify(paymentRepository).findById("payment-3");
    }

    @Test
    void getAllPaymentsDelegatesToRepository() {
        List<Payment> expected = List.of(
                buildPayment("payment-1", buildOrder("order-1")),
                buildPayment("payment-2", buildOrder("order-2"))
        );
        when(paymentRepository.findAll()).thenReturn(expected);

        List<Payment> result = paymentService.getAllPayments();

        assertEquals(2, result.size());
        assertEquals("payment-1", result.get(0).getId());
        assertEquals("payment-2", result.get(1).getId());
        verify(paymentRepository).findAll();
    }

    @Test
    void addPaymentRejectsNullOrder() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");

        assertThrows(IllegalArgumentException.class, () ->
                paymentService.addPayment(null, "Voucher Code", paymentData)
        );
    }

    private Order buildOrder(String id) {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sample");
        product.setProductQuantity(1);

        List<Product> products = new ArrayList<>();
        products.add(product);
        return new Order(id, products, 1708560000L, "Tester");
    }

    private Payment buildPayment(String id, Order order) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setOrder(order);
        payment.setMethod("Voucher Code");
        payment.setStatus(Payment.STATUS_PENDING);
        return payment;
    }
}
