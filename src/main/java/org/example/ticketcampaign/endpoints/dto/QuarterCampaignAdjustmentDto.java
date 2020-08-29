package org.example.ticketcampaign.endpoints.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class QuarterCampaignAdjustmentDto {

    private int adjustment;
    private LocalDate referenceDate = LocalDate.now();
}
