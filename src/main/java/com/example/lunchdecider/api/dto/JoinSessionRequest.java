package com.example.lunchdecider.api.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinSessionRequest(@NotBlank String username) {}
