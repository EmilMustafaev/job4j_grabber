package ru.job4j.grabber;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection connection;

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty("jdbc.driver"));
            String url = config.getProperty("db.url");
            String username = config.getProperty("db.username");
            String password = config.getProperty("db.password");
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Post createPost(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("link"),
                resultSet.getString("text_vacan"),
                resultSet.getTimestamp("created").toLocalDateTime());
    }

    @Override
    public void save(Post post) {
        String sql = "INSERT INTO post(name, link, text_vacan, created)"
                + "VALUES(?, ?, ?, ?)"
                + "ON CONFLICT (link) DO UPDATE SET "
                + "name = EXCLUDED.name, "
                + "text_vacan = EXCLUDED.text_vacan, "
                + "created = EXCLUDED.created";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getLink());
            preparedStatement.setString(3, post.getDescription());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM post";

        try (Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Post post = createPost(resultSet);
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        String sql = "SELECT * FROM post WHERE id = ?";
        Post post = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    post = createPost(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}
