package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.ScenarioActionId;

public interface ScenarioActionRepository
        extends JpaRepository<ScenarioAction, ScenarioActionId> {

    @Modifying
    @Query("delete from ScenarioAction sa where sa.sensor.id = :sensorId")
    void deleteAllBySensorId(@Param("sensorId") String sensorId);
}
