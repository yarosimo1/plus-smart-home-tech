package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.model.ScenarioAction;

public interface ScenarioActionRepository
        extends JpaRepository<ScenarioAction, Long> {
//    @Modifying
//    @Query("delete from ScenarioAction sa where sa.sensor.id = :sensorId")
    void deleteAllBySensorId(@Param("sensorId") String sensorId);

//    @Modifying
//    @Query("delete from ScenarioAction sa where sa.scenario.id = :scenarioId")
    void deleteAllByScenarioId(@Param("scenarioId") Long scenarioId);
}
