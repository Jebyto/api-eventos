package com.eventos.api.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eventos.api.domain.event.Event;
import com.eventos.api.domain.event.EventDetailsDTO;
import com.eventos.api.domain.event.EventRequestDTO;
import com.eventos.api.domain.event.EventResponseDTO;
import com.eventos.api.domain.coupon.CouponDTO;
import com.eventos.api.repositories.EventRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AddressService addressService;
    private final CouponService couponService;
    private final S3Client s3Service;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public Event createEvent(EventRequestDTO eventRequestDTO) {
        String imgUrl = null;

        if (eventRequestDTO.image() != null && !eventRequestDTO.image().isEmpty()) {
            this.uploadImage(eventRequestDTO.image());
        }

        Event newEvent = new Event();
        newEvent.setTitle(eventRequestDTO.title());
        newEvent.setDescription(eventRequestDTO.description());
        newEvent.setDate(new Date(eventRequestDTO.date()));
        newEvent.setRemote(eventRequestDTO.remote());
        newEvent.setEventUrl(eventRequestDTO.eventUrl());
        newEvent.setImgUrl(imgUrl);

        this.eventRepository.save(newEvent);

        if (!eventRequestDTO.remote()) {
            this.addressService.createAddress(eventRequestDTO, newEvent);
        }

        return newEvent;
    }

    public List<EventResponseDTO> getUpcomingEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.findUpcomingEvents(new Date(), pageable);

        return eventPage.stream()
                .map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl(),
                        event.getDate()))
                .toList();
    }

    public List<EventResponseDTO> getFilteredEvents(
            int page, int size,
            String title,
            String city,
            String state,
            Date startDate,
            Date endDate) {

        title = (title != null && !title.isEmpty()) ? title : null;
        city = (city != null && !city.isEmpty()) ? city : null;
        state = (state != null && !state.isEmpty()) ? state : null;
        startDate = (startDate != null) ? startDate : null;
        endDate = (endDate != null) ? endDate : null;

        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.findFilteredEvents(title, city, state, startDate, endDate, pageable);

        return eventPage.stream()
                .map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl(),
                        event.getDate()))
                .toList();
    }

    public EventDetailsDTO getEventDetails(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        List<CouponDTO> coupons = couponService.getValidCouponsByEvent(event).stream()
                .map(coupon -> new CouponDTO(
                        coupon.getCode(),
                        coupon.getDiscount(),
                        coupon.getValid()))
                .toList();

        return new EventDetailsDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getRemote(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getUf() : "",
                event.getEventUrl(),
                event.getImgUrl(),
                coupons);
    }

    private String uploadImage(MultipartFile image) {
        String imgName = UUID.randomUUID() + "-" + image.getOriginalFilename();

        try {
            File file = this.convertMultiPartToFile(image);

            s3Service.putObject(builder -> builder.bucket(this.bucketName).key(imgName).build(),
                    RequestBody.fromFile(file));

            file.delete();

            return s3Service.utilities().getUrl(builder -> builder.bucket(this.bucketName).key(imgName).build())
                    .toString();
        } catch (IOException | AwsServiceException | SdkClientException e) {
            System.err.println("Error uploading image to S3: " + e.getMessage());
        }
        return "";
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }
        File convertedFile = new File(file.getOriginalFilename());

        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }

        return convertedFile;
    }
}
