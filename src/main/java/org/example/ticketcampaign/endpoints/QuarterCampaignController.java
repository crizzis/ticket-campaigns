package org.example.ticketcampaign.endpoints;

import lombok.RequiredArgsConstructor;
import org.example.ticketcampaign.domain.QuarterCampaignException;
import org.example.ticketcampaign.domain.QuarterCampaignFacade;
import org.example.ticketcampaign.endpoints.dto.ErrorDto;
import org.example.ticketcampaign.endpoints.dto.QuarterCampaignAdjustmentDto;
import org.example.ticketcampaign.endpoints.dto.QuarterCampaignCreationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/quartercampaigns")
@RequiredArgsConstructor
public class QuarterCampaignController {

    private final QuarterCampaignFacade facade;

    @PostMapping
    @ResponseStatus(CREATED)
    long create(@RequestBody QuarterCampaignCreationDto campaign) {
        return facade.create(campaign.getReferenceDate(), campaign.getTicketPool());
    }

    @GetMapping
    ResponseEntity<Map<String, Integer>> findByDate(@RequestParam LocalDate referenceDate) {
        return ResponseEntity.of(facade.findByReferenceDate(referenceDate)
                .map(this::toWeekMap));
    }

    @GetMapping("/{id}")
    ResponseEntity<Map<String, Integer>> findById(@PathVariable Long id) {
        return ResponseEntity.of(facade.findById(id)
                .map(this::toWeekMap));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    void delete(@PathVariable Long id) {
        facade.deleteById(id);
    }

    @PostMapping("/adjust")
    @ResponseStatus(NO_CONTENT)
    void adjust(@RequestBody QuarterCampaignAdjustmentDto adjustmentData) {
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
}
