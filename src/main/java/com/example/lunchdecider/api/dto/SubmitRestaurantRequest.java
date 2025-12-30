package com.example.lunchdecider.api.dto;

import jakarta.validation.constraints.NotBlank;

public record SubmitRestaurantRequest(
        @NotBlank String username,
        @NotBlank String restaurantName
) {}
