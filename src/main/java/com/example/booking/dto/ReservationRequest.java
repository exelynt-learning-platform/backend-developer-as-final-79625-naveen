package com.example.booking.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data public class ReservationRequest {
    @NotNull private Long resourceId;
    @NotNull @FutureOrPresent private LocalDateTime startTime;
    @NotNull @FutureOrPresent private LocalDateTime endTime;
    @NotNull @DecimalMin("0.0") private BigDecimal price;
}
