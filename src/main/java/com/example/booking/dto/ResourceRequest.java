package com.example.booking.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
@Data public class ResourceRequest {
    @NotBlank private String name;
    private String description;
    @NotBlank private String type;
    @NotNull private Boolean active;
    @NotNull @DecimalMin("0.0") private BigDecimal defaultPrice;
}
