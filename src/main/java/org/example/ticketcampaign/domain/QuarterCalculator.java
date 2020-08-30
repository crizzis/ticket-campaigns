package org.example.ticketcampaign.domain;

import java.time.LocalDate;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.*;

class QuarterCalculator {

    private static final int MONTHS_PER_QUARTER = 3;
    private static final int QUARTERS_PER_YEAR = 4;

    DateInterval resolveQuarterForDate(LocalDate referenceDate) {
        int quarterIndex = (referenceDate.getMonthValue() - 1) / MONTHS_PER_QUARTER;
        int year = referenceDate.getYear();
        if (isBoundaryMonth(referenceDate) && doesWeekEndNextMonth(referenceDate)) {
            quarterIndex += 1;
        }
        if (quarterIndex >= QUARTERS_PER_YEAR) {
            quarterIndex -= QUARTERS_PER_YEAR;
            year += 1;
        }
        return buildIntervalForQuarter(quarterIndex, year);
    }

    private DateInterval buildIntervalForQuarter(int quarterIndex, int year) {
        int startingMonth = 1 + quarterIndex * MONTHS_PER_QUARTER;
        return DateInterval.builder()
                .startDate(firstDayOfStartingWeek(startingMonth, year))
                .endDate(endOfLastFullWeek(startingMonth + MONTHS_PER_QUARTER - 1, year))
                .build();
    }

    private LocalDate endOfLastFullWeek(int month, int year) {
        return LocalDate.of(year, month, 1).with(lastInMonth(SUNDAY));
    }

    private LocalDate firstDayOfStartingWeek(int month, int year) {
        return LocalDate.of(year, month, 1).with(previousOrSame(MONDAY));
    }

    private boolean doesWeekEndNextMonth(LocalDate referenceDate) {
        return !referenceDate.getMonth().equals(referenceDate.with(nextOrSame(SUNDAY)).getMonth());
    }

    private boolean isBoundaryMonth(LocalDate referenceDate) {
        return (referenceDate.getMonthValue() % MONTHS_PER_QUARTER) == 0;
    }
}
