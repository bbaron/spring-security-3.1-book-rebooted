package app.dataaccess;

import app.domain.CalendarUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A jdbc implementation of {@link CalendarUserDao}.
 *
 * @author Rob Winch
 */
@Repository
public class JdbcCalendarUserDao implements CalendarUserDao {

    // --- members ---

    private final JdbcOperations jdbcOperations;

    // --- constructors ---

    @Autowired
    public JdbcCalendarUserDao(JdbcOperations jdbcOperations) {
        if (jdbcOperations == null) {
            throw new IllegalArgumentException("jdbcOperations cannot be null");
        }
        this.jdbcOperations = jdbcOperations;
    }

    // --- CalendarUserDao methods ---

    @Override
    @Transactional(readOnly = true)
    public CalendarUser getUser(int id) {
        try {
            return jdbcOperations.queryForObject(CALENDAR_USER_QUERY + "id = ?", CalendarUserRowMapper.USER, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarUser findUserByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("email cannot be null");
        }
        try {
            return jdbcOperations.queryForObject(CALENDAR_USER_QUERY + "email = ?", CalendarUserRowMapper.USER, email);
        } catch (EmptyResultDataAccessException notFound) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarUser> findUsersByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("email cannot be null");
        }
        if ("".equals(email)) {
            throw new IllegalArgumentException("email cannot be empty string");
        }
        return jdbcOperations.query(CALENDAR_USER_QUERY + "email like ? order by id", CalendarUserRowMapper.USER, email + "%");
    }

    @Override
    public int createUser(final CalendarUser userToAdd) {
        if (userToAdd == null) {
            throw new IllegalArgumentException("userToAdd cannot be null");
        }
        if (userToAdd.getId() != null) {
            throw new IllegalArgumentException("userToAdd.getId() must be null when creating a " + CalendarUser.class.getName());
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into calendar_users (email, password, first_name, last_name) values (?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, userToAdd.getEmail());
            ps.setString(2, userToAdd.getPassword());
            ps.setString(3, userToAdd.getFirstName());
            ps.setString(4, userToAdd.getLastName());
            return ps;
        }, keyHolder);
        return requireNonNull(keyHolder.getKey()).intValue();
    }

    // --- non-public static members ---

    private static final String CALENDAR_USER_QUERY = "select id, email, password, first_name, last_name from calendar_users where ";


    /**
     * Create a new RowMapper that resolves {@link CalendarUser}'s given a column label prefix. By allowing the prefix
     * to be specified we can reuse the same {@link RowMapper} for joins in other tables.
     *
     * @author Rob Winch
     */
    @RequiredArgsConstructor
    enum CalendarUserRowMapper implements RowMapper<CalendarUser> {
        USER("calendar_users."),
        OWNER("owner_"),
        ATTENDEE("owner_");

        private final String columnLabelPrefix;

        @Override
        public CalendarUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            return CalendarUser.builder()
                    .id(rs.getInt(columnLabelPrefix + "id"))
                    .email(rs.getString(columnLabelPrefix + "email"))
                    .password(rs.getString(columnLabelPrefix + "password"))
                    .firstName(rs.getString(columnLabelPrefix + "first_name"))
                    .lastName(rs.getString(columnLabelPrefix + "last_name"))
                    .build();
        }
    }
}