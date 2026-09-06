package com.example.booking.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data public class ResourceResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Boolean active;
    private BigDecimal defaultPrice;
}
