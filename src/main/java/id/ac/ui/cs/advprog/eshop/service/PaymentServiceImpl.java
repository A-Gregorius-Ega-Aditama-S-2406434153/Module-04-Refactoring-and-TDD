package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = Objects.requireNonNull(paymentRepository, "paymentRepository must not be null");
    }

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        if (order == null) {
            throw new IllegalArgumentException("order must not be null");
        }
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setPaymentData(paymentData);

        String initialStatus = evaluateInitialStatus(method, payment.getPaymentData());
        setStatus(payment, initialStatus);

        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        if (payment == null) {
            throw new IllegalArgumentException("payment must not be null");
        }
        payment.setStatus(status);
        if (payment.getOrder() == null) {
            return payment;
        }
        if (Payment.STATUS_SUCCESS.equals(status)) {
            payment.getOrder().setStatus(OrderStatus.SUCCESS.getValue());
        } else if (Payment.STATUS_REJECTED.equals(status)) {
            payment.getOrder().setStatus(OrderStatus.FAILED.getValue());
        }
        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private String evaluateInitialStatus(String method, Map<String, String> paymentData) {
        if (isVoucherMethod(method)) {
            return isValidVoucherCode(paymentData.get("voucherCode"))
                    ? Payment.STATUS_SUCCESS
                    : Payment.STATUS_REJECTED;
        }
        if (isCashOnDeliveryMethod(method)) {
            return isValidCashOnDeliveryData(paymentData)
                    ? Payment.STATUS_SUCCESS
                    : Payment.STATUS_REJECTED;
        }
        if (isBankTransferMethod(method)) {
            return isValidBankTransferData(paymentData)
                    ? Payment.STATUS_SUCCESS
                    : Payment.STATUS_REJECTED;
        }
        return Payment.STATUS_REJECTED;
    }

    private boolean isValidVoucherCode(String voucherCode) {
        if (!hasText(voucherCode)) {
            return false;
        }
        if (voucherCode.length() != 16) {
            return false;
        }
        if (!voucherCode.startsWith("ESHOP")) {
            return false;
        }
        int numberCount = 0;
        for (char currentChar : voucherCode.toCharArray()) {
            if (Character.isDigit(currentChar)) {
                numberCount++;
            }
        }
        return numberCount == 8;
    }

    private boolean isValidCashOnDeliveryData(Map<String, String> paymentData) {
        return hasText(paymentData.get("address"))
                && hasText(paymentData.get("deliveryFee"));
    }

    private boolean isValidBankTransferData(Map<String, String> paymentData) {
        return hasText(paymentData.get("bankName"))
                && hasText(paymentData.get("referenceCode"));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isVoucherMethod(String method) {
        String normalizedMethod = normalizeMethod(method);
        return "voucher".equals(normalizedMethod) || "vouchercode".equals(normalizedMethod);
    }

    private boolean isCashOnDeliveryMethod(String method) {
        String normalizedMethod = normalizeMethod(method);
        return "cod".equals(normalizedMethod) || "cashondelivery".equals(normalizedMethod);
    }

    private boolean isBankTransferMethod(String method) {
        return "banktransfer".equals(normalizeMethod(method));
    }

    private String normalizeMethod(String method) {
        if (method == null) {
            return "";
        }
        return method.toLowerCase().replaceAll("[^a-z]", "");
    }
}
