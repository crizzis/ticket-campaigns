package org.example.ticketcampaign.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Entity
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@Getter(PACKAGE)
@Builder(access = PRIVATE)
class QuarterCampaign {

    private static final int WEEKS_PER_QUARTER = 13;

    @Id
    @GeneratedValue
    @Setter(PACKAGE)
    private Long id;

    @Setter(PACKAGE)
    private DateInterval timeframe;

    @ElementCollection
    @OrderColumn
    private List<Integer> weeklyTickets;

    static QuarterCampaign forDateAndTicketPool(LocalDate referenceDate, int ticketPool) {
        if (ticketPool < 0) {
            throw new QuarterCampaignException("Initial ticket pool must not be negative");
        }
        if (isNull(referenceDate)) {
            throw new QuarterCampaignException("Reference date must be provided");
        }
        return QuarterCampaign.builder()
                .timeframe(new QuarterCalculator().resolveQuarterForDate(referenceDate))
                .weeklyTickets(splitEvenly(ticketPool, WEEKS_PER_QUARTER))
                .build();
    }

    int getTicketsAvailableInWeek(int weekIndex) {
        if (weekIndex >= weeklyTickets.size()) {
            throw new QuarterCampaignException("weekIndex out of bounds");
        }
        return weeklyTickets.get(weekIndex);
    }

    void adjustBy(int ticketPool) {
        this.weeklyTickets = adjustBy(weeklyTickets, ticketPool);
    }

    private static List<Integer> adjustBy(List<Integer> weeklyTickets, int ticketPool) {
        var result = new ArrayList<Integer>();
        int quotient = ticketPool / weeklyTickets.size();
        int remainder = Math.abs(ticketPool % weeklyTickets.size());
        boolean reduction = (ticketPool < 0);
        for (int i = 0; i < weeklyTickets.size(); ++i) {
            int adjustedValue = weeklyTickets.get(i) + quotient;
            if (i < remainder) {
                adjustedValue += (reduction ? -1 : 1);
            }
            result.add(Math.max(0, adjustedValue));
        }
        return result;
    }

    private static List<Integer> splitEvenly(int ticketPool, int size) {
        return adjustBy(Collections.nCopies(size, 0), ticketPool);
    }
}
