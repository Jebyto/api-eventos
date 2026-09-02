package com.eventos.api.services;

import com.eventos.api.domain.coupon.Coupon;
import com.eventos.api.domain.coupon.CouponRequestDTO;
import com.eventos.api.domain.event.Event;
import com.eventos.api.repositories.CouponRepository;
import com.eventos.api.repositories.EventRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final EventRepository eventRepository;

    public Coupon addCouponToEvent(UUID eventId, CouponRequestDTO couponRequestDTO) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        Coupon coupon = new Coupon();
        coupon.setCode(couponRequestDTO.code());
        coupon.setDiscount(couponRequestDTO.discount());
        coupon.setValid(new Date(couponRequestDTO.valid()));
        coupon.setEvent(event);

        return couponRepository.save(coupon);
    }

    public List<Coupon> getValidCouponsByEvent(Event event) {
        return couponRepository.findByEventAndValidAfter(event, new Date());
    }
}
