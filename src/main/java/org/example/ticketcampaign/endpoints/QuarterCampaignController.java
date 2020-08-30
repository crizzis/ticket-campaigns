package org.example.ticketcampaign.endpoints;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.example.ticketcampaign.domain.QuarterCampaignException;
import org.example.ticketcampaign.domain.QuarterCampaignFacade;
import org.example.ticketcampaign.endpoints.dto.ErrorDto;
import org.example.ticketcampaign.endpoints.dto.QuarterCampaignAdjustmentDto;
import org.example.ticketcampaign.endpoints.dto.QuarterCampaignCreationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/quartercampaigns")
@RequiredArgsConstructor
@Validated
@Api("Managing quarterly promo ticket campaigns")
public class QuarterCampaignController {

    private final QuarterCampaignFacade facade;
    private final Clock clock;

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation("Create a campaign for a given quarter and reference date")
    long create(@Valid @RequestBody QuarterCampaignCreationDto campaign) {
        return facade.create(resolveReferenceDate(campaign), campaign.getTicketPool());
    }

    @GetMapping
    @ApiOperation("Find a quarter by the given reference date")
    @ApiResponse(code = 200, message = "Quarter found", examples =
        @Example(@ExampleProperty(mediaType = "*/*", value = "{'week1': 4, 'week2': 4, 'week13': 3}")))
    ResponseEntity<Map<String, Integer>> findByDate(@RequestParam LocalDate referenceDate) {
        return ResponseEntity.of(facade.findByReferenceDate(referenceDate)
                .map(this::toWeekMap));
    }

    @GetMapping("/{id}")
    @ApiOperation("Find a quarter with the given id")
    @ApiResponse(code = 200, message = "Quarter found", examples =
        @Example(@ExampleProperty(mediaType = "*/*", value = "{'week1': 4, 'week2': 4, 'week13': 3}")))
    ResponseEntity<Map<String, Integer>> findById(@PathVariable Long id) {
        return ResponseEntity.of(facade.findById(id)
                .map(this::toWeekMap));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation("Delete a quarter by its id")
    void delete(@PathVariable Long id) {
        facade.deleteById(id);
    }

    @PostMapping("/adjust")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation("Adjust the ticket pool by distributing extra tickets evenly between all the weeks in a quarter")
    void adjust(@Valid @RequestBody QuarterCampaignAdjustmentDto adjustmentData) {
        facade.adjust(adjustmentData.getReferenceDate(), adjustmentData.getAdjustment());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(QuarterCampaignException.class)
    ErrorDto handleBusinessException(QuarterCampaignException exception) {
        return ErrorDto.builder()
                .message(exception.getMessage())
                .build();
    }

    private Map<String, Integer> toWeekMap(List<Integer> weeklyTickets) {
        return IntStream.range(0, weeklyTickets.size())
                .boxed()
                .collect(toOrderedMapKeyedByWeekNames(weeklyTickets));
    }

    private Collector<Integer, ?, LinkedHashMap<String, Integer>> toOrderedMapKeyedByWeekNames(List<Integer> weeklyTickets) {
        return Collectors.toMap(
                index -> "week" + (index + 1),
                weeklyTickets::get,
                (left, right) -> left,
                LinkedHashMap::new);
    }

    private LocalDate resolveReferenceDate(QuarterCampaignCreationDto campaign) {
        return nonNull(campaign.getReferenceDate()) ? campaign.getReferenceDate() : LocalDate.now(clock);
    }
}
