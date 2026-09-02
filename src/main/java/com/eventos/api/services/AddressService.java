package com.eventos.api.services;

import org.springframework.stereotype.Service;

import com.eventos.api.domain.address.Address;
import com.eventos.api.domain.event.Event;
import com.eventos.api.domain.event.EventRequestDTO;
import com.eventos.api.repositories.AddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public Address createAddress(EventRequestDTO eventRequestDTO, Event event) {
        Address address = new Address();
        address.setCity(eventRequestDTO.city());
        address.setUf(eventRequestDTO.state());
        address.setEvent(event);

        return addressRepository.save(address);
    }
}
