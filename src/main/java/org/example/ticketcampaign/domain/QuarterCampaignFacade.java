package org.example.ticketcampaign.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class QuarterCampaignFacade {

    private final QuarterCampaignRepository quarterCampaignRepository;

    public Long create(LocalDate referenceDate, int totalTickets) {
        if (quarterCampaignRepository.existsByReferenceDate(referenceDate)) {
            throw new QuarterCampaignException("Campaign already exists for referenceDate: " + referenceDate);
        }
        return quarterCampaignRepository
                .saveAndFlush(QuarterCampaign.forDateAndTicketPool(referenceDate, totalTickets))
                .getId();
    }

    public void adjust(LocalDate referenceDate, int adjustment) {
        if (Objects.isNull(referenceDate)) {
            throw new QuarterCampaignException("Missing reference date");
        }
        quarterCampaignRepository.findByReferenceDate(referenceDate)
                .orElseThrow(() -> new QuarterCampaignException("Could not find campaign by reference date: " + referenceDate))
                .adjustBy(adjustment);
    }
}
