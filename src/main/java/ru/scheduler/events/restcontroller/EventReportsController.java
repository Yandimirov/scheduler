package ru.scheduler.events.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.AbstractJasperReportsView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsXlsView;
import ru.scheduler.events.model.dto.EventReportRow;
import ru.scheduler.events.model.entity.Event;
import ru.scheduler.events.service.EventService;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Mikhail Yandimirov on 27.05.2018.
 */

@RestController
@RequestMapping(value = "/reports")
public class EventReportsController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ApplicationContext appContext;

    @RequestMapping("/event/{id}")
    public ModelAndView collectEventReport(@RequestParam(name = "type", required = false, defaultValue = "PDF") String reportType, @PathVariable long id) {

        AbstractJasperReportsView view;

        if ("XLS".equalsIgnoreCase(reportType)) {
            view = new JasperReportsXlsView();
        } else if ("PDF".equalsIgnoreCase(reportType)) {
            view = new JasperReportsPdfView();
        } else {
            throw new IllegalArgumentException("Incorrect report type " + reportType);
        }

        view.setUrl("classpath:event_report.jrxml");
        view.setApplicationContext(appContext);

        List<Event> events = eventService.getAllVersions(id);
        List<EventReportRow> reportRows = events.stream()
                .map(EventReportRow::fromEvent)
                .sorted(Comparator.comparingLong(EventReportRow::getVersion))
                .collect(Collectors.toList());

        Map<String, Object> params = new HashMap<>();
        params.put("datasource", reportRows);
        params.put("format", reportType.toLowerCase());

        return new ModelAndView(view, params);
    }
}
