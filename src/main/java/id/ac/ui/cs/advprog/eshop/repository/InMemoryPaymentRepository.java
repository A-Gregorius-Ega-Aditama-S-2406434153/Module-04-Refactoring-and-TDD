package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    private final List<Payment> paymentData = new ArrayList<>();

    @Override
    public Payment save(Payment payment) {
        if (payment == null) {
            return null;
        }
        for (int i = 0; i < paymentData.size(); i++) {
            Payment storedPayment = paymentData.get(i);
            if (isSamePaymentId(storedPayment, payment)) {
                paymentData.set(i, payment);
                return payment;
            }
        }
        paymentData.add(payment);
        return payment;
    }

    @Override
    public Payment findById(String paymentId) {
        for (Payment payment : paymentData) {
            if (payment.getId() != null && payment.getId().equals(paymentId)) {
                return payment;
            }
        }
        return null;
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(paymentData);
    }

    private boolean isSamePaymentId(Payment firstPayment, Payment secondPayment) {
        return firstPayment.getId() != null && firstPayment.getId().equals(secondPayment.getId());
    }
}
