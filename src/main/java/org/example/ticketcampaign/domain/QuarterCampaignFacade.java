package org.example.ticketcampaign.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Transactional(readOnly = true)
    public Optional<List<Integer>> findById(Long id) {
        return asWeeklyTicketCounts(quarterCampaignRepository.findById(id));
    }

    @Transactional(readOnly = true)
    public Optional<List<Integer>> findByReferenceDate(LocalDate referenceDate) {
        return asWeeklyTicketCounts(quarterCampaignRepository.findByReferenceDate(referenceDate));
    }

    public void deleteById(Long id) {
        quarterCampaignRepository.deleteById(id);
    }

    private Optional<List<Integer>> asWeeklyTicketCounts(Optional<QuarterCampaign> campaign) {
        return campaign
                .map(QuarterCampaign::getWeeklyTickets)
                .map(List::copyOf);
    }
}
