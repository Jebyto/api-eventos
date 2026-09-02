package com.eventos.api.domain.event;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.eventos.api.domain.coupon.CouponDTO;

public record EventDetailsDTO(
        UUID id,
        String title,
        String description,
        Date date,
        Boolean remote,
        String city,
        String state,
        String eventUrl,
        String imgUrl,
        List<CouponDTO> coupons) {

}
