package com.eventos.api.repositories;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventos.api.domain.coupon.Coupon;
import com.eventos.api.domain.event.Event;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    List<Coupon> findByEventAndValidAfter(Event event, Date currentDate);
}
