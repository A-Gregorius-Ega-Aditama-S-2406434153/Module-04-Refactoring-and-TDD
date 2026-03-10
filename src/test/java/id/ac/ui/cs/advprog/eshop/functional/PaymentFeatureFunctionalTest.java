package id.ac.ui.cs.advprog.eshop.functional;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentFeatureFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Test
    void getPaymentDetailPageReturnsDetailFormTemplate() throws Exception {
        mockMvc.perform(get("/payment/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("PaymentDetail"));
    }

    @Test
    void getPaymentDetailByIdReturnsPaymentInfoTemplate() throws Exception {
        Payment payment = createVoucherPayment();

        mockMvc.perform(get("/payment/detail/{paymentId}", payment.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("PaymentInfo"))
                .andExpect(model().attributeExists("payment"));
    }

    @Test
    void getPaymentAdminListReturnsAllPaymentsTemplate() throws Exception {
        createVoucherPayment();

        mockMvc.perform(get("/payment/admin/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("PaymentAdminList"))
                .andExpect(model().attributeExists("payments"))
                .andExpect(model().attribute("payments", not(empty())));
    }

    @Test
    void postSetStatusUpdatesPaymentStatus() throws Exception {
        Payment payment = createVoucherPayment();

        mockMvc.perform(post("/payment/admin/set-status/{paymentId}", payment.getId())
                        .param("status", Payment.STATUS_REJECTED))
                .andExpect(status().isOk())
                .andExpect(view().name("PaymentAdminDetail"))
                .andExpect(model().attributeExists("payment"));
    }

    private Payment createVoucherPayment() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        Product product = new Product();
        product.setProductId("product-" + uniqueSuffix);
        product.setProductName("Product " + uniqueSuffix);
        product.setProductQuantity(1);

        List<Product> products = new ArrayList<>();
        products.add(product);

        Order order = new Order(
                "order-" + uniqueSuffix,
                products,
                System.currentTimeMillis(),
                "author-" + uniqueSuffix
        );
        orderService.createOrder(order);

        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        return paymentService.addPayment(order, "Voucher Code", paymentData);
    }
}
