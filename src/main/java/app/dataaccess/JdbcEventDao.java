package app.dataaccess;

import app.domain.CalendarUser;
import app.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static app.dataaccess.JdbcCalendarUserDao.CalendarUserRowMapper;
import static java.util.Objects.requireNonNull;

/**
 * A jdbc implementation of {@link EventDao}.
 *
 * @author Rob Winch
 */
@Repository
public class JdbcEventDao implements EventDao {

    // --- members ---

    private final JdbcOperations jdbcOperations;

    // --- constructors ---

    @Autowired
    public JdbcEventDao(JdbcOperations jdbcOperations) {
        if (jdbcOperations == null) {
            throw new IllegalArgumentException("jdbcOperations cannot be null");
        }
        this.jdbcOperations = jdbcOperations;
    }

    // --- EventService ---

    @Override
    @Transactional(readOnly = true)
    public Event getEvent(int eventId) {
        try {
            return jdbcOperations.queryForObject(EVENT_QUERY + " and e.id = ?",
                    this::eventRowMapper, eventId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int createEvent(final Event event) {
        if (event == null) {
            throw new IllegalArgumentException("event cannot be null");
        }
        if (event.getId() != null) {
            throw new IllegalArgumentException("event.getId() must be null when creating a new Message");
        }
        var owner = event.getOwner();
        if (owner == null) {
            throw new IllegalArgumentException("event.getOwner() cannot be null");
        }
        var attendee = event.getAttendee();
        if (attendee == null) {
            throw new IllegalArgumentException("attendee.getOwner() cannot be null");
        }
        var when = event.getWhen();
        if (when == null) {
            throw new IllegalArgumentException("event.getWhen() cannot be null");
        }
        var keyHolder = new GeneratedKeyHolder();
        this.jdbcOperations.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into events (when,summary,description,owner,attendee) values (?, ?, ?, ?, ?)",
                    new String[]{"id"});

            @SuppressWarnings("deprecation")
            var dt = new java.sql.Date(when.getYear() - 1900,
                    when.getMonthValue() - 1, when.getDayOfMonth());
            ps.setDate(1, dt);
            ps.setString(2, event.getSummary());
            ps.setString(3, event.getDescription());
            ps.setInt(4, owner.getId());
            ps.setObject(5, attendee.getId());
            return ps;
        }, keyHolder);
        return requireNonNull(keyHolder.getKey()).intValue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findForUser(int userId) {
        return jdbcOperations.query(EVENT_QUERY + " and (e.owner = ? or e.attendee = ?) order by e.id",
                this::eventRowMapper, userId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEvents() {
        return jdbcOperations.query(EVENT_QUERY + " order by e.id",
                this::eventRowMapper);
    }

    // --- non-public static members ---

    /**
     * A RowMapper for mapping a {@link Event}
     */
    private Event eventRowMapper(ResultSet rs, int rowNum) throws SQLException {
        CalendarUser attendee = ATTENDEE_ROW_MAPPER.mapRow(rs, rowNum);
        CalendarUser owner = OWNER_ROW_MAPPER.mapRow(rs, rowNum);

        var event = Event.builder();
        event.id(rs.getInt("events.id"));
        event.summary(rs.getString("events.summary"));
        event.description(rs.getString("events.description"));
        var when = rs.getTimestamp("events.when").toLocalDateTime();
        event.when(when);
        event.attendee(attendee);
        event.owner(owner);
        return event.build();
    }


    private static final RowMapper<CalendarUser> ATTENDEE_ROW_MAPPER = CalendarUserRowMapper.ATTENDEE;
    private static final RowMapper<CalendarUser> OWNER_ROW_MAPPER = CalendarUserRowMapper.OWNER;

    private static final String EVENT_QUERY = "select e.id, e.summary, e.description, e.when, " +
            "owner.id as owner_id, owner.email as owner_email, owner.password as owner_password, owner.first_name as owner_first_name, owner.last_name as owner_last_name, " +
            "attendee.id as attendee_id, attendee.email as attendee_email, attendee.password as attendee_password, attendee.first_name as attendee_first_name, attendee.last_name as attendee_last_name " +
            "from events as e, calendar_users as owner, calendar_users as attendee " +
            "where e.owner = owner.id and e.attendee = attendee.id";
}