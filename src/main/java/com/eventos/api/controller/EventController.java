package com.eventos.api.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventos.api.domain.event.Event;
import com.eventos.api.domain.event.EventDetailsDTO;
import com.eventos.api.domain.event.EventRequestDTO;
import com.eventos.api.domain.event.EventResponseDTO;
import com.eventos.api.services.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Event> createEvent(@RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("date") long date,
            @RequestParam("remote") boolean remote,
            @RequestParam(value = "eventUrl", required = false) String eventUrl,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "state", required = false) String state) {

        EventRequestDTO eventRequestDTO = new EventRequestDTO(title, description, date, remote, eventUrl, image, city,
                state);
        Event createdEvent = this.eventService.createEvent(eventRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getEvents(@RequestParam int pag, @RequestParam int size) {
        List<EventResponseDTO> events = this.eventService.getUpcomingEvents(pag, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailsDTO> getEventDetails(@PathVariable UUID eventId) {
        EventDetailsDTO event = this.eventService.getEventDetails(eventId);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<EventResponseDTO>> getFilteredEvents(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        List<EventResponseDTO> events = this.eventService.getFilteredEvents(page, size, city, state, title, startDate,
                endDate);
        return ResponseEntity.ok(events);
    }
}
