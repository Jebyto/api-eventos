package com.eventos.api.domain.event;

import java.util.Date;
import java.util.UUID;

public record EventResponseDTO(UUID id, String title, String description, String city, String state, Boolean remote,
        String eventUrl, String imgUrl, Date date) {

}
