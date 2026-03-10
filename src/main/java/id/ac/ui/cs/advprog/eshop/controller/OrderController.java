package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController {

    private static final String REDIRECT_ORDER_HISTORY = "redirect:/order/history";
    private static final String PAYMENT_METHOD_VOUCHER = "Voucher Code";
    private static final String PAYMENT_METHOD_COD = "Cash on Delivery";

    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/create")
    public String createOrderPage() {
        return "CreateOrder";
    }

    @PostMapping("/create")
    public String createOrderPost(
            @RequestParam("author") String author,
            @RequestParam("productName") String productName,
            @RequestParam("productQuantity") int productQuantity
    ) {
        if (!StringUtils.hasText(author) || !StringUtils.hasText(productName) || productQuantity <= 0) {
            return "redirect:/order/create";
        }

        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName(productName);
        product.setProductQuantity(productQuantity);

        List<Product> products = new ArrayList<>();
        products.add(product);

        Order order = new Order(
                UUID.randomUUID().toString(),
                products,
                System.currentTimeMillis(),
                author
        );
        orderService.createOrder(order);
        return REDIRECT_ORDER_HISTORY;
    }

    @GetMapping("/history")
    public String orderHistoryPage() {
        return "OrderHistory";
    }

    @PostMapping("/history")
    public String orderHistoryPost(@RequestParam("author") String author, Model model) {
        List<Order> orders = orderService.findAllByAuthor(author);
        model.addAttribute("author", author);
        model.addAttribute("orders", orders);
        return "OrderList";
    }

    @GetMapping("/pay/{orderId}")
    public String orderPayPage(@PathVariable String orderId, Model model) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return REDIRECT_ORDER_HISTORY;
        }
        model.addAttribute("order", order);
        model.addAttribute("voucherMethod", PAYMENT_METHOD_VOUCHER);
        model.addAttribute("codMethod", PAYMENT_METHOD_COD);
        return "OrderPay";
    }

    @PostMapping("/pay/{orderId}")
    public String orderPayPost(
            @PathVariable String orderId,
            @RequestParam("method") String method,
            @RequestParam(value = "voucherCode", required = false) String voucherCode,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "deliveryFee", required = false) String deliveryFee,
            Model model
    ) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return REDIRECT_ORDER_HISTORY;
        }

        Map<String, String> paymentData = new LinkedHashMap<>();
        if (PAYMENT_METHOD_VOUCHER.equals(method)) {
            paymentData.put("voucherCode", voucherCode);
        } else if (PAYMENT_METHOD_COD.equals(method)) {
            paymentData.put("address", address);
            paymentData.put("deliveryFee", deliveryFee);
        }

        Payment payment = paymentService.addPayment(order, method, paymentData);
        model.addAttribute("payment", payment);
        return "OrderPayResult";
    }
}
