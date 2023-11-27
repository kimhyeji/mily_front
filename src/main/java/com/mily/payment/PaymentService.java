package com.mily.payment;

import com.mily.base.rsData.RsData;
import com.mily.user.MilyUser;
import com.mily.user.MilyUserRepository;
import com.mily.user.MilyUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final MilyUserService milyUserService;
    private final PaymentRepository paymentRepository;
    private final MilyUserRepository milyUserRepository;

    public RsData<Payment> doPayment (String orderId, MilyUser milyUser, String orderName, Long amount) {
        LocalDateTime now = LocalDateTime.now();

        Payment payment = Payment.builder()
                .orderId(orderId)
                .orderDate(now)
                .orderName(orderName)
                .customerName(milyUser)
                .amount(amount)
                .build();

        payment = paymentRepository.save(payment);
        return RsData.of("S-1", "결제 성공", payment);
    }

    public RsData<Payment> dummyPayment (String orderId, MilyUser milyUser, int point, String orderName, Long amount) {
        LocalDateTime now = LocalDateTime.now();

        Payment payment = Payment.builder()
                .orderId(orderId)
                .orderDate(now)
                .orderName(orderName)
                .customerName(milyUser)
                .amount(amount)
                .milyPoint(point)
                .build();

        payment = paymentRepository.save(payment);
        return RsData.of("S-1", "결제 성공", payment);
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public String letFormattedAmount (Long amount) {
        if (amount == null) {
            return "0";
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        return numberFormat.format(amount);
    }
}