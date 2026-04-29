package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.model.ScenarioConditionId;

public interface ScenarioConditionRepository
        extends JpaRepository<ScenarioCondition, ScenarioConditionId> {

    @Modifying
    @Query("delete from ScenarioCondition sc where sc.sensor.id = :sensorId")
    void deleteAllBySensorId(@Param("sensorId") String sensorId);


}
