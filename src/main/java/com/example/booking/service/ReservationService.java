package com.example.booking.service;

import com.example.booking.dto.ReservationRequest;
import com.example.booking.dto.ReservationResponse;
import com.example.booking.entity.AppUser;
import com.example.booking.entity.BookableResource;
import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Role;
import com.example.booking.exception.ApiExceptions;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, ResourceRepository resourceRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().isEqual(request.getEndTime())) {
            throw new ApiExceptions.BadRequestException("Start time must be before end time");
        }

        BookableResource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ApiExceptions.NotFoundException("Resource not found"));
        
        if (!resource.getActive()) {
            throw new ApiExceptions.BadRequestException("Resource is not active");
        }

        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                resource.getId(), request.getStartTime(), request.getEndTime());
        if (!overlapping.isEmpty()) {
            throw new ApiExceptions.ConflictException("Overlapping reservation exists");
        }

        AppUser currentUser = getCurrentUser();
        
        Reservation reservation = Reservation.builder()
                .resource(resource)
                .user(currentUser)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .status(ReservationStatus.PENDING)
                .build();
                
        return mapToResponse(reservationRepository.save(reservation));
    }

    @Transactional(readOnly = true)
    public Page<ReservationResponse> getReservations(ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        AppUser currentUser = getCurrentUser();
        Specification<Reservation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (currentUser.getRole() != Role.ADMIN) {
                predicates.add(cb.equal(root.get("user"), currentUser));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return reservationRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiExceptions.NotFoundException("Reservation not found"));
        
        verifyOwnershipOrAdmin(reservation);
        return mapToResponse(reservation);
    }

    @Transactional
    public ReservationResponse updateReservation(Long id, ReservationRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiExceptions.NotFoundException("Reservation not found"));
                
        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().isEqual(request.getEndTime())) {
            throw new ApiExceptions.BadRequestException("Start time must be before end time");
        }

        BookableResource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ApiExceptions.NotFoundException("Resource not found"));

        List<Reservation> overlapping = reservationRepository.findOverlappingReservations(
                resource.getId(), request.getStartTime(), request.getEndTime());
        
        boolean hasConflict = overlapping.stream().anyMatch(r -> !r.getId().equals(reservation.getId()));
        if (hasConflict) {
            throw new ApiExceptions.ConflictException("Overlapping reservation exists");
        }

        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPrice(request.getPrice());
        
        return mapToResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiExceptions.NotFoundException("Reservation not found"));
        
        verifyOwnershipOrAdmin(reservation);
        reservation.setStatus(ReservationStatus.CANCELLED);
        return mapToResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ApiExceptions.NotFoundException("Reservation not found");
        }
        reservationRepository.deleteById(id);
    }

    private AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiExceptions.NotFoundException("User not found"));
    }
    
    private void verifyOwnershipOrAdmin(Reservation reservation) {
        AppUser currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && !reservation.getUser().getId().equals(currentUser.getId())) {
            throw new ApiExceptions.ForbiddenException("Cannot access another user's reservation");
        }
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setResourceId(reservation.getResource().getId());
        response.setUsername(reservation.getUser().getUsername());
        response.setStartTime(reservation.getStartTime());
        response.setEndTime(reservation.getEndTime());
        response.setPrice(reservation.getPrice());
        response.setStatus(reservation.getStatus().name());
        return response;
    }
}
