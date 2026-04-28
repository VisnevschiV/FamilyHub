package com.visnevschi.familyhub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.dbenitity.PeriodRecord;

@Repository
public interface PeriodRecordRepository extends JpaRepository<PeriodRecord, Long> {
    List<PeriodRecord> findAllByOrderByProfileIdAscStartDateAscIdAsc();

    List<PeriodRecord> findByProfileIdOrderByStartDateAscIdAsc(Long profileId);

    boolean existsByProfileIdAndStartDate(Long profileId, java.time.LocalDate startDate);

    Optional<PeriodRecord> findFirstByProfileIdAndEndDateIsNullOrderByStartDateDescIdDesc(Long profileId);

    Optional<PeriodRecord> findFirstByProfileIdOrderByStartDateDescIdDesc(Long profileId);

    Optional<PeriodRecord> findFirstByProfileIdAndEndDateIsNotNullOrderByEndDateDescIdDesc(Long profileId);

    List<PeriodRecord> findTop6ByProfileIdOrderByStartDateDescIdDesc(Long profileId);

        @Query("""
                        select pr
                        from PeriodRecord pr
                        where pr.profile.id = :profileId
                            and pr.startDate <= :monthEnd
                            and (pr.endDate is null or pr.endDate >= :monthStart)
                        order by pr.startDate asc, pr.id asc
                        """)
        List<PeriodRecord> findMonthRecordsByProfileId(@Param("profileId") Long profileId,
                                                                                                     @Param("monthStart") java.time.LocalDate monthStart,
                                                                                                     @Param("monthEnd") java.time.LocalDate monthEnd);
}
