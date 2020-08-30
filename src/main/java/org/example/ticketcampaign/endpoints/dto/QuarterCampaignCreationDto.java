package org.example.ticketcampaign.endpoints.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Getter
public class QuarterCampaignCreationDto {

    @PositiveOrZero
    @ApiModelProperty("The total number of tickets to be distributed evenly among all the weeks in the quarter")
    private int ticketPool;

    @ApiModelProperty("A date that falls within the quarter to create; if not provided, defaults to the current date")
    private LocalDate referenceDate;
}
