package org.example.ticketcampaign.domain

import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

import static java.time.Month.DECEMBER
import static java.time.Month.FEBRUARY
import static java.time.Month.JUNE
import static java.time.Month.MARCH
import static java.time.Month.NOVEMBER

@Unroll
class QuarterCampaignFacadeTest extends Specification {

    private static final long NEWLY_CREATED_ID = 1L
    /* assumption: weeks are assigned to quarters based on the month in which they end
        - a week crossing the boundary of two quarters belongs to the new quarter
     */
    private static final LocalDate START_FIRST_QUARTER = LocalDate.of(2019, DECEMBER, 30)
    private static final LocalDate MID_FIRST_QUARTER = LocalDate.of(2020, FEBRUARY, 11)
    private static final LocalDate START_SECOND_QUARTER = LocalDate.of(2020, MARCH, 30)
    private static final LocalDate START_THIRD_QUARTER = LocalDate.of(2020, JUNE, 29)
    private static final LocalDate MID_FOURTH_QUARTER = LocalDate.of(2020, NOVEMBER, 20)

    private QuarterCampaignRepository repository = Mock()
    private QuarterCampaignFacade facade = new QuarterCampaignFacade(repository)

    private QuarterCampaign createdCampaign

    def setup() {
        repository.saveAndFlush(_) >> { QuarterCampaign campaign ->
            campaign.id = NEWLY_CREATED_ID
            createdCampaign = campaign
        }
        repository.existsByReferenceDate(MID_FIRST_QUARTER) >> false
        repository.findByReferenceDate(MID_FIRST_QUARTER) >> Optional.empty()
        repository.existsByReferenceDate(MID_FOURTH_QUARTER) >> true
    }

    def "create should create quarter campaign when not exists for given reference date"() {
        when:
        def newCampaignId = facade.create(MID_FIRST_QUARTER, 13)

        then:
        newCampaignId == NEWLY_CREATED_ID
    }

    def "create should report error when #description"() {
        when:
        facade.create(date, totalTickets)

        then:
        thrown QuarterCampaignException

        where:
        date               | totalTickets || description
        null               | 10           || 'missing reference date'
        MID_FIRST_QUARTER  | -5           || 'ticket pool negative'
        MID_FOURTH_QUARTER | 10           || 'quarter already exists'
    }

    def "create should assign start date #startDate and end date #endDate to campaign when reference date is #referenceDate"() {
        when:
        facade.create(referenceDate, 13)

        then:
        createdCampaign.timeframe.startDate == startDate
        createdCampaign.timeframe.endDate == endDate

        where:
        referenceDate                     || startDate            | endDate
        START_FIRST_QUARTER               || START_FIRST_QUARTER  | START_SECOND_QUARTER.minusDays(1)
        MID_FIRST_QUARTER                 || START_FIRST_QUARTER  | START_SECOND_QUARTER.minusDays(1)
        START_SECOND_QUARTER.minusDays(1) || START_FIRST_QUARTER  | START_SECOND_QUARTER.minusDays(1)
        START_SECOND_QUARTER.minusDays(3) || START_FIRST_QUARTER  | START_SECOND_QUARTER.minusDays(1)
        START_SECOND_QUARTER              || START_SECOND_QUARTER | START_THIRD_QUARTER.minusDays(1)
    }

    def "create should split the ticket pool of #totalTickets evenly between all weeks giving #expected"() {
        when:
        facade.create(START_FIRST_QUARTER, totalTickets)

        then:
        expected.eachWithIndex { expectedPerWeek, i ->
            assert createdCampaign.getTicketsAvailableInWeek(i) == expectedPerWeek
        }

        where:
        totalTickets || expected
        0            || [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        26           || [2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2]
        10           || [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0]
        35           || [3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2]
    }

    def "adjust should report error when #description"() {
        when:
        facade.adjust(MID_FIRST_QUARTER, 10)

        then:
        thrown QuarterCampaignException

        where:
        referenceDate     | adjustment || description
        null              | 10         || 'missing campaign'
        MID_FIRST_QUARTER | 10         || 'campaign not exists'
    }

    def "adjust should increase or decrease initial ticket number #initialPool evenly giving #expected when adjustment by #adjustment"() {
        given:
        def campaign = QuarterCampaign.forDateAndTicketPool(MID_FOURTH_QUARTER, initialPool)
        repository.findByReferenceDate(MID_FOURTH_QUARTER) >> Optional.of(campaign)

        when:
        facade.adjust(MID_FOURTH_QUARTER, adjustment)

        then:
        expected.eachWithIndex { expectedPerWeek, i ->
            assert campaign.getTicketsAvailableInWeek(i) == expectedPerWeek
        }

        where:
        initialPool | adjustment || expected
        0           | 0          || [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        26          | 0          || [2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2]
        0           | 26         || [2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2]
        0           | -26        || [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        26          | 10         || [3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2]
        13          | 35         || [4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3]
        26          | -26        || [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        26          | -13        || [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
        26          | -10        || [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2]
        26          | -35        || [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    }
}
