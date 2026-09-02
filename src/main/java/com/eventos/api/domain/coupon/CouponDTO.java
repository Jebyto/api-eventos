package com.eventos.api.domain.coupon;

import java.util.Date;

public record CouponDTO(String code, Integer discount, Date valid) {

}
