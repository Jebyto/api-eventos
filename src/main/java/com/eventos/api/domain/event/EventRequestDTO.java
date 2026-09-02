package com.eventos.api.domain.event;

import org.springframework.web.multipart.MultipartFile;

public record EventRequestDTO(String title, String description, Long date, Boolean remote, String eventUrl,
                MultipartFile image, String city, String state) {

}
