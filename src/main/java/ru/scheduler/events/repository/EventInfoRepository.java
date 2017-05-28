package ru.scheduler.events.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.scheduler.events.model.entity.EventInfo;

/**
 * Created by Mikhail Yandimirov on 16.04.2017.
 */
@Repository
public interface EventInfoRepository extends CrudRepository<EventInfo, Long>{

}
