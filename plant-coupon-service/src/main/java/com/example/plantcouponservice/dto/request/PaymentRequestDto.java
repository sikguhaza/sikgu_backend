package com.example.plantcouponservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto implements Serializable {
    @NotNull
    private Integer payMoney;
    private Integer discountPrice;

    @NotNull
    private Integer memberNo;
    private Long couponNo;
    private Integer sellerNo;
    private CouponStatus couponStatus;
}
