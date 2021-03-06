package by.epam.filmrating.dao;

import by.epam.filmrating.connection.DBConnectionPool;
import by.epam.filmrating.entity.Comment;
import by.epam.filmrating.exception.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class CommentDAO extends AbstractDAO<Comment> {
    private final static String DELETE_COMMENT = "DELETE FROM COMMENT WHERE COMMENT_ID = ?";
    private final static String INSERT_COMMENT = "INSERT INTO COMMENT(COMMENT_ID, TEXT, FILM_ID, USER_ID, CREATION_DATE) VALUES(?,?,?,?,?)";
    private final static String SELECT_COMMENTS_BY_FILM = "SELECT COMMENT_ID, TEXT, FILM_ID, USER_ID, CREATION_DATE FROM COMMENT WHERE FILM_ID = ? ORDER BY CREATION_DATE DESC";

    private final static String COMMENT_ID = "COMMENT_ID";
    private final static String TEXT = "TEXT";
    private final static String FILM_ID = "FILM_ID";
    private final static String USER_ID = "USER_ID";
    private final static String TIME_FORMAT = "GMT";
    private final static String CREATION_DATE = "CREATION_DATE";

    public CommentDAO() {
        this.connectionPool = DBConnectionPool.getInstance();
    }

    @Override
    public boolean delete(int id) throws DAOException {
        return super.deleteHandler(id, DELETE_COMMENT);
    }

    @Override
    public boolean create(Comment comment) throws DAOException {
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement preparedStatement = connectionPool.getPreparedStatement(INSERT_COMMENT, connection)) {
            preparedStatement.setInt(1, comment.getCommentId());
            preparedStatement.setString(2, comment.getText());
            preparedStatement.setInt(3, comment.getFilmId());
            preparedStatement.setInt(4, comment.getUserId());
            preparedStatement.setTimestamp(5, new Timestamp(comment.getCreationDate().getTime()),
                    Calendar.getInstance(TimeZone.getTimeZone(TIME_FORMAT)));
            return preparedStatement.execute();
        } catch (SQLException ex) {
            throw new DAOException("Error while executing create comment method.", ex);
        } finally {
            this.closeConnection(connection);
        }
    }

    @Override
    public List<Comment> findEntitiesByFilm(int filmId) throws DAOException {
        List<Comment> comments = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        try (PreparedStatement preparedStatement = connectionPool.getPreparedStatement(SELECT_COMMENTS_BY_FILM, connection)) {
            preparedStatement.setInt(1, filmId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Comment comment = new Comment(resultSet.getInt(COMMENT_ID), resultSet.getString(TEXT), resultSet.getInt(FILM_ID), resultSet.getInt(USER_ID), resultSet.getTimestamp(CREATION_DATE));
                    comments.add(comment);
                }
            }
        } catch (SQLException ex) {
            throw new DAOException("Error while executing findCommentsByFilm method.", ex);
        } finally {
            this.closeConnection(connection);
        }
        return comments;
    }

    @Override
    public List<Comment> findAll() throws DAOException {
        throw new DAOException("This method is not implemented.");
    }

    @Override
    public Comment findEntityBySign(int id) throws DAOException {
        throw new DAOException("This method is not implemented.");
    }

    @Override
    public Comment findEntityBySign(String name) throws DAOException {
        throw new DAOException("This method is not implemented.");
    }
}
