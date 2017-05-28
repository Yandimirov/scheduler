package ru.scheduler.service.unit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.scheduler.events.model.entity.EventInfo;
import ru.scheduler.events.repository.EventInfoRepository;
import ru.scheduler.events.service.EventInfoService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventInfoServiceTest {
    @Mock
    EventInfoRepository eventInfoRepository;

    @InjectMocks
    EventInfoService eventInfoService;

    EventInfo eventInfo;

    @Before
    public void setUp() throws Exception {
        eventInfo = new EventInfo();
        eventInfo.setId(1L);
    }

    @Test
    public void addEventInfo() throws Exception {
        when(eventInfoRepository.save(eventInfo)).thenReturn(eventInfo);
        eventInfoService.addEventInfo(eventInfo);
        verify(eventInfoRepository).save(eventInfo);
    }

    @Test
    public void getEventInfo() throws Exception {
        when(eventInfoRepository.findOne(eventInfo.getId())).thenReturn(eventInfo);
        eventInfoService.getEventInfo(eventInfo.getId());
        verify(eventInfoRepository).findOne(eventInfo.getId());
    }

}