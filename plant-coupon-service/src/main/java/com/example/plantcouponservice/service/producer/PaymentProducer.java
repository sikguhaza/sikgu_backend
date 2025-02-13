package com.example.plantcouponservice.service.producer;

import com.example.plantcouponservice.dto.request.PaymentRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {
    private final KafkaTemplate<String, PaymentRequestDto> kafkaTemplate;


    public void create(PaymentRequestDto paymentRequestDto) {
        log.info("쿠폰 사용 이벤트: {}", paymentRequestDto.getCouponNo());
        kafkaTemplate.send("payment", paymentRequestDto);
    }


}
