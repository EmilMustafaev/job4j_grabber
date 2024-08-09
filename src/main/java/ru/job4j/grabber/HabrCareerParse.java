package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    public static final int COUNT_PAGES = 5;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Element descriptionElement = document.select(".vacancy-description__text").first();
        return descriptionElement != null ? descriptionElement.text() : "";
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        try {
            for (int pageNumber = 1; pageNumber <= COUNT_PAGES; pageNumber++) {
                String fullLink = "%s%s%d%s".formatted(link, PREFIX, pageNumber, SUFFIX);
                Connection connection = Jsoup.connect(fullLink);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                for (Element row : rows) {
                    Element titleElement = row.select(".vacancy-card__title").first();
                    Element linkElement = titleElement.child(0);
                    Element dateElement = row.select(".basic-date").first();
                    String vacancyName = titleElement.text();
                    String vacancyLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                    String date = dateElement.attr("datetime");
                    LocalDateTime created = dateTimeParser.parse(date);
                    String description = retrieveDescription(vacancyLink);
                    Post post = new Post(0, vacancyName, vacancyLink, description, created);
                    posts.add(post);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public static void main(String[] args) throws IOException {
        for (int pageNumber = 1; pageNumber <= COUNT_PAGES; pageNumber++) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".basic-date").first();
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String date = dateElement.attr("datetime");
                System.out.printf("%s (%s) %s%n", vacancyName, date, link);
            });
        }
    }

}