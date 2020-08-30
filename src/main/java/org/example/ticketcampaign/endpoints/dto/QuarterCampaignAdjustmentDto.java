package org.example.ticketcampaign.endpoints.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
public class QuarterCampaignAdjustmentDto {

    @ApiModelProperty("Total number of tickets to add to the quarter campaign; " +
            "use negative numbers to subtract from the pool instead")
    private int adjustment;

    @NotNull
    @ApiModelProperty("A date that falls within the quarter to adjust")
    private LocalDate referenceDate;
}
