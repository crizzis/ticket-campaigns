package org.example.ticketcampaign.domain;

import lombok.*;

import javax.persistence.Embeddable;

import java.time.LocalDate;

import static lombok.AccessLevel.PACKAGE;

@Getter(PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
class DateInterval {

    private LocalDate startDate;
    private LocalDate endDate;
}
