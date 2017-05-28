package ru.scheduler.events.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.scheduler.events.model.entity.Place;
import ru.scheduler.events.repository.PlaceRepository;

/**
 * Created by Mikhail Yandimirov on 20.04.2017.
 */

@Service
public class PlaceService {

    @Autowired
    PlaceRepository placeRepository;

    public Place addPlace(Place place){
        return placeRepository.save(place);
    }

    public Place findById(String id){
        return placeRepository.findOne(id);
    }
}

