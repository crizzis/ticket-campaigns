package org.example.ticketcampaign.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

interface QuarterCampaignRepository extends JpaRepository<QuarterCampaign, Long> {

    @Query("SELECT q " +
            "FROM QuarterCampaign q " +
            "WHERE :referenceDate BETWEEN q.timeframe.startDate AND q.timeframe.endDate")
    Optional<QuarterCampaign> findByReferenceDate(@Param("referenceDate") LocalDate referenceDate);

    @Query("SELECT CASE WHEN COUNT(q.id) > 0 THEN TRUE ELSE FALSE END " +
            "FROM QuarterCampaign q " +
            "WHERE :referenceDate BETWEEN q.timeframe.startDate AND q.timeframe.endDate")
    boolean existsByReferenceDate(@Param("referenceDate") LocalDate referenceDate);
}
