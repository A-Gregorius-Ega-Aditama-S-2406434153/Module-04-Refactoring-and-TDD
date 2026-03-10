package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private static final String REDIRECT_PAYMENT_DETAIL = "redirect:/payment/detail";
    private static final String REDIRECT_PAYMENT_ADMIN_LIST = "redirect:/payment/admin/list";

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/detail")
    public String paymentDetailPage() {
        return "PaymentDetail";
    }

    @GetMapping("/detail/{paymentId}")
    public String paymentDetailByIdPage(@PathVariable String paymentId, Model model) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return REDIRECT_PAYMENT_DETAIL;
        }
        model.addAttribute("payment", payment);
        return "PaymentInfo";
    }

    @GetMapping("/admin/list")
    public String paymentAdminListPage(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "PaymentAdminList";
    }

    @GetMapping("/admin/detail/{paymentId}")
    public String paymentAdminDetailPage(@PathVariable String paymentId, Model model) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return REDIRECT_PAYMENT_ADMIN_LIST;
        }
        addPaymentAdminDetailModel(model, payment);
        return "PaymentAdminDetail";
    }

    @PostMapping("/admin/set-status/{paymentId}")
    public String paymentAdminSetStatus(
            @PathVariable String paymentId,
            @RequestParam("status") String status,
            Model model
    ) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return REDIRECT_PAYMENT_ADMIN_LIST;
        }
        paymentService.setStatus(payment, status);
        addPaymentAdminDetailModel(model, payment);
        return "PaymentAdminDetail";
    }

    private void addPaymentAdminDetailModel(Model model, Payment payment) {
        model.addAttribute("payment", payment);
        model.addAttribute("successStatus", Payment.STATUS_SUCCESS);
        model.addAttribute("rejectedStatus", Payment.STATUS_REJECTED);
    }
}
