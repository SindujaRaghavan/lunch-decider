package com.example.lunchdecider.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSessionRequest(@NotBlank String createdByUsername) {}
