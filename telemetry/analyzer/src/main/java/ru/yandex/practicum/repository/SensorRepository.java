package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.entity.Sensor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, String> {

    boolean existsByIdInAndHubId(Collection<String> ids, String hubId);

    Optional<Sensor> findByIdAndHubId(String id, String hubId);

    @EntityGraph(attributePaths = {
            "conditions",
            "conditions.condition",
            "actions",
            "actions.action"
    })
    List<Scenario> findByHubId(String hubId);
}
