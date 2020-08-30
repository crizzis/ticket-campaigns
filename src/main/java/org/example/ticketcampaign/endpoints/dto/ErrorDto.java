package org.example.ticketcampaign.endpoints.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ErrorDto {

    private final String message;
}
