package ru.scheduler.service.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.scheduler.events.model.entity.Place;
import ru.scheduler.events.repository.PlaceRepository;
import ru.scheduler.events.service.PlaceService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PlaceServiceTest {
    @Mock
    PlaceRepository placeRepository;

    @InjectMocks
    PlaceService placeService;

    Place place;

    @Before
    public void before(){
        place = Place.builder()
                .name("123")
                .id("1")
                .lat(1.0)
                .lon(1.0)
                .build();
    }

    @Test
    public void addPlace() throws Exception {
        when(placeRepository.save(place)).thenReturn(place);
        placeService.addPlace(place);
        verify(placeRepository).save(place);
    }

    @Test
    public void findById() throws Exception {
        when(placeRepository.findOne(place.getId())).thenReturn(place);
        placeService.findById(place.getId());
        verify(placeRepository).findOne(place.getId());
    }

}