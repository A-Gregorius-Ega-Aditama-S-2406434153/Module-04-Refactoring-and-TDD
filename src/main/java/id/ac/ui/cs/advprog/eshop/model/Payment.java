package id.ac.ui.cs.advprog.eshop.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Payment {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_REJECTED = "REJECTED";

    private String id;
    private Order order;
    private String method;
    private String status = STATUS_PENDING;
    private Map<String, String> paymentData = new HashMap<>();

    public void setPaymentData(Map<String, String> paymentData) {
        if (paymentData == null) {
            this.paymentData = new HashMap<>();
            return;
        }
        this.paymentData = new HashMap<>(paymentData);
    }
}
