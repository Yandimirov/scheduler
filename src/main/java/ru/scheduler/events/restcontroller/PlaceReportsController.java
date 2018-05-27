package ru.scheduler.events.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;
import ru.scheduler.events.repository.PlaceRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mikhail Yandimirov on 27.05.2018.
 */

@RestController
@RequestMapping(value = "/reports")
public class PlaceReportsController {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    protected PlaceRepository placeRepository;

    @GetMapping("/place")
    public ModelAndView getPlaceReport() {

        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:report.jrxml");
        view.setApplicationContext(appContext);


        Map<String, Object> params = new HashMap<>();
        params.put("datasource", placeRepository.findAll());

        return new ModelAndView(view, params);
    }
}
