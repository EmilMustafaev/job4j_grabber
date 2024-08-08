package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;

class HabrCareerDateTimeParserTest {

    @Test
    void whenParseDateThenReturnLocalDateTime() {
        String input = "2023-11-14T13:05:48+03:00";
        LocalDateTime expected = LocalDateTime
                .of(2023, 11, 14, 13, 05, 48);
        DateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse(input);
        assertEquals(expected, result);
    }

    @Test
    void whenParseDateThenReturnLocalDateTimeTwo() {
        String input = "2021-09-11T16:09:23+03:00";
        LocalDateTime expected = LocalDateTime
                .of(2021, 9, 11, 16, 9, 23);
        DateTimeParser parser = new HabrCareerDateTimeParser();
        LocalDateTime result = parser.parse(input);
        assertEquals(expected, result);
    }
}