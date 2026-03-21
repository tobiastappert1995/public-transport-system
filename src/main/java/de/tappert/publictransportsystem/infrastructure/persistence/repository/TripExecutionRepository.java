package de.tappert.publictransportsystem.infrastructure.persistence.repository;

import de.tappert.publictransportsystem.domain.model.TripExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripExecutionRepository extends JpaRepository<TripExecution, Long> {

    @Query("""
            select distinct te
            from TripExecution te
            where te.executionTime >= :from
              and te.executionTime <= :until
            order by te.executionTime asc
            """)
    List<TripExecution> findByTimeRange(@Param("from") LocalDateTime from, @Param("until") LocalDateTime until);

    @Query("""
            select distinct te
            from TripExecution te
            join te.trip t
            join t.travelPaths tp
            join tp.travelPathStops tps
            join tps.stop s
            where lower(s.location) like lower(concat('%', :location, '%'))
              and te.executionTime >= :from
              and te.executionTime <= :until
            order by te.executionTime asc
            """)
    List<TripExecution> findByLocationAndTimeRange(
            @Param("location") String location,
            @Param("from") LocalDateTime from,
            @Param("until") LocalDateTime until
    );
}

