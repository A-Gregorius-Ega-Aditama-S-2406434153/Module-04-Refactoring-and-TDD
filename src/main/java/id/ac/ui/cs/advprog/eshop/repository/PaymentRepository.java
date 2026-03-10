package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;

import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);

    Payment findById(String paymentId);

    List<Payment> findAll();
}
