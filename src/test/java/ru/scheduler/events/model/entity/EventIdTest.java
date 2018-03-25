package ru.scheduler.events.model.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;

public class EventIdTest {

    @Test
    public void test() throws IOException {
        String josn = "{\n"
                + "  \"endDate\": \"2018-03-25T12:59:16.769Z\",\n"
                + "  \"id\": 1,\n"
                + "  \"info\": {\n"
                + "    \"description\": \"string\",\n"
                + "    \"id\": 0,\n"
                + "    \"name\": \"string\",\n"
                + "    \"place\": {\n"
                + "      \"description\": \"string\",\n"
                + "      \"id\": \"string\",\n"
                + "      \"lat\": 0,\n"
                + "      \"lon\": 0,\n"
                + "      \"name\": \"string\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"startDate\": \"2018-03-25T12:59:16.769Z\",\n"
                + "  \"type\": \"APPROVED\"\n"
                + "}";

        Event event = new ObjectMapper().readValue(josn, Event.class);

        System.out.println(event.getId());
    }
}