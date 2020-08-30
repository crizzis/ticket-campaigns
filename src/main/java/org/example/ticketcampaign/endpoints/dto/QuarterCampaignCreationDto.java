package org.example.ticketcampaign.endpoints.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class QuarterCampaignCreationDto {

    private int ticketPool;
    private LocalDate referenceDate;
}
