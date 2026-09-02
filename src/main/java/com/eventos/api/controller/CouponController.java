package com.eventos.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventos.api.domain.coupon.Coupon;
import com.eventos.api.domain.coupon.CouponRequestDTO;
import com.eventos.api.services.CouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/events/{eventId}")
    public ResponseEntity<Coupon> addCouponToEvent(@PathVariable UUID eventId,
            @RequestBody CouponRequestDTO couponRequestDTO) {

        Coupon coupon = couponService.addCouponToEvent(eventId, couponRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(coupon);
    }
}
