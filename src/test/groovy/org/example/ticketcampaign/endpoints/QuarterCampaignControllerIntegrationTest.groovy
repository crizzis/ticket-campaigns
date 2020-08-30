package org.example.ticketcampaign.endpoints


import org.example.ticketcampaign.domain.QuarterCampaignRepository
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

import static java.time.Month.APRIL
import static java.time.Month.FEBRUARY
import static java.time.Month.MAY
import static org.springframework.http.HttpStatus.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

@Sql("/scripts/quarter_campaign_init.sql")
class QuarterCampaignControllerIntegrationTest extends BaseControllerIntegrationTest {

    private static final LocalDate SECOND_QUARTER_DATE = LocalDate.of(2020, APRIL, 2)
    private static final LocalDate TODAY = LocalDate.of(2020, FEBRUARY, 18)

    @SpringBean
    private Clock defaultClock = Stub() {
        instant() >> TODAY.atStartOfDay(ZoneId.systemDefault()).toInstant()
        getZone() >> ZoneId.systemDefault()
    }

    @Autowired
    private QuarterCampaignRepository repository

    def "create should report success when correct request"() {
        when:
        def response = performWithJsonContent(post('/quartercampaigns'), [
                'ticketPool'   : 26,
                'referenceDate': '2020-01-24'])
        def content = fromJson(response.contentAsString) as Long

        then:
        response.status == CREATED.value()
        content != null
        repository.existsById(content)
    }

    def "create should use current date if reference date not explicitly provided"() {
        when:
        def response = performWithJsonContent(post('/quartercampaigns'), ['ticketPool' : 26])

        then:
        response.status == CREATED.value()
        repository.existsByReferenceDate(TODAY)
    }

    def "create should report error when campaign already exists"() {
        when:
        def response = performWithJsonContent(post('/quartercampaigns'), [
                'ticketPool'   : 26,
                'referenceDate': '2020-10-03'])
        def content = fromJson(response.contentAsString)

        then:
        response.status == BAD_REQUEST.value()
        content.message == 'Campaign already exists for referenceDate: 2020-10-03'
    }

    def "create should report error when ticket pool negative"() {
        when:
        def response = performWithJsonContent(post('/quartercampaigns'), [
                'ticketPool'   : -26,
                'referenceDate': '2020-01-24'])
        def content = fromJson(response.contentAsString)

        then:
        response.status == BAD_REQUEST.value()
        content.message == 'ticketPool: must be greater than or equal to 0'
    }

    def "findByDate should return correct weekly ticket counts when campaign exists"() {
        when:
        def response = perform(get('/quartercampaigns').param('referenceDate', '2020-11-01'))
        def content = fromJson(response.contentAsString)

        then:
        content == [
                week1: 10, week2: 10, week3: 10, week4: 10,
                week5: 9, week6: 9, week7: 9, week8: 9,
                week9: 9, week10: 9, week11: 9, week12: 9,
                week13: 9
        ]
    }

    def "findByDate should report status NOT_FOUND when campaign not exists"() {
        expect:
        perform(get('/quartercampaigns')
                .param('referenceDate', '2020-01-24')).status == NOT_FOUND.value()
    }

    def "adjust should report error when campaign not exists"() {
        when:
        def result = performWithJsonContent(post("/quartercampaigns/adjust"), [
                'referenceDate': '2020-01-24',
                'adjustment': 13
        ])
        def response = fromJson(result.contentAsString)

        then:
        result.status == BAD_REQUEST.value()
        response.message == 'Could not find campaign by reference date: 2020-01-24'
    }

    def "adjust should report error when missing reference date"() {
        when:
        def response = performWithJsonContent(post('/quartercampaigns/adjust'), [
                'ticketPool'   : -26])
        def content = fromJson(response.contentAsString)

        then:
        response.status == BAD_REQUEST.value()
        content.message == 'referenceDate: must not be null'
    }

    def "adjust should report success when campaign exists"() {
        when:
        def result = performWithJsonContent(post("/quartercampaigns/adjust"), [
                'referenceDate': '2020-05-12',
                'adjustment': 13
        ])
        def adjustedCampaign = repository.findByReferenceDate(LocalDate.of(2020, MAY, 12))

        then:
        result.status == NO_CONTENT.value()
        adjustedCampaign.get().weeklyTickets == [4] * 13
    }

    def "findById should report status NOT_FOUND when campaign not exists"() {
        expect:
        perform(get('/quartercampaigns/{id}', 0)).status == NOT_FOUND.value()
    }

    def "findById should return existing campaign"() {
        given:
        def existingId = getSecondQuarterCampaignId()

        when:
        def response = perform(get('/quartercampaigns/{id}', existingId))
        def content = fromJson(response.contentAsString)

        then:
        content == [
                week1: 3, week2: 3, week3: 3, week4: 3,
                week5: 3, week6: 3, week7: 3, week8: 3,
                week9: 3, week10: 3, week11: 3, week12: 3,
                week13: 3
        ]
    }

    def "delete should remove existing campaign"() {
        given:
        def existingId = getSecondQuarterCampaignId()

        when:
        def response = perform(delete('/quartercampaigns/{id}', existingId))

        then:
        response.status == NO_CONTENT.value()
        !repository.existsByReferenceDate(SECOND_QUARTER_DATE)
    }

    private long getSecondQuarterCampaignId() {
        repository.findByReferenceDate(SECOND_QUARTER_DATE).get().id
    }

}
