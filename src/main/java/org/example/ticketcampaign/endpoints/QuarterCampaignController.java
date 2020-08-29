package org.example.ticketcampaign.endpoints;

import org.example.ticketcampaign.endpoints.dto.QuarterCampaignAdjustmentDto;
import org.example.ticketcampaign.endpoints.dto.QuarterCampaignCreationDto;
import org.example.ticketcampaign.endpoints.dto.QuarterCampaignDto;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/quartercampaigns")
public class QuarterCampaignController {

    @PostMapping
    @ResponseStatus(CREATED)
    long create(QuarterCampaignCreationDto campaign) {
        return 0L;
    }

    @GetMapping
    QuarterCampaignDto findByDate(@RequestParam LocalDate campaignDate) {
        return null;
    }

    @GetMapping("/{id}")
    QuarterCampaignDto findById(@PathVariable Long id) {
        return null;
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id) {
    }

    @PostMapping("/adjust")
    QuarterCampaignDto adjust(QuarterCampaignAdjustmentDto adjustment) {
        return null;
    }
}
