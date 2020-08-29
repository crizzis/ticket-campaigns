package org.example.ticketcampaign.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;

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
}
