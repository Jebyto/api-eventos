package com.eventos.api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventos.api.domain.address.Address;

public interface AddressRepository extends JpaRepository<Address, UUID> {

}
