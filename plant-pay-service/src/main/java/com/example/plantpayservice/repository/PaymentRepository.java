package com.example.plantpayservice.repository;

import com.example.plantpayservice.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> , CustomPaymentRepository{
    Payment findByMemberNo(Integer memberNo);

    boolean existsByMemberNo(Integer memberNo);

//    void tradePayMoney(Payment sellerPayment, Payment buyerPayment, PaymentRequestDto paymentRequestDto);
}
