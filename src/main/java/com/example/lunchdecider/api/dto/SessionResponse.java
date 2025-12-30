package com.example.lunchdecider.api.dto;

import java.util.List;

public record SessionResponse(
        String code,
        String status,
        String createdBy,
        List<String> participants,
        List<String> restaurants,
        String pickedRestaurant
) {}
