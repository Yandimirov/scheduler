package ru.scheduler.events.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.scheduler.events.model.entity.Place;

/**
 * Created by Mikhail Yandimirov on 09.04.2017.
 */

@Repository
public interface PlaceRepository extends CrudRepository<Place, String> {
}
