package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    static String INSERT_MSG;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        INSERT_MSG = "INSERT INTO time_entries (project_id, user_id, date, hours)  VALUES(?,?,?,?)";

       KeyHolder keyHolder = new GeneratedKeyHolder();
       jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_MSG,
                            RETURN_GENERATED_KEYS);
            ps.setLong(1, timeEntry.getProjectId());
            ps.setLong(2, timeEntry.getUserId());
            ps.setDate(3, java.sql.Date.valueOf(timeEntry.getDate()));
            ps.setInt(4, timeEntry.getHours());
            return ps;
        }, keyHolder);

        return find(keyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {
        return jdbcTemplate.query(
                "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?",
                new Object[]{id},
                extractor);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        final String UPDATE_MSG = "UPDATE time_entries " +
                "SET project_id = ?, user_id = ?, date = ?,  hours = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(UPDATE_MSG, timeEntry.getProjectId(), timeEntry.getUserId(), java.sql.Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(), id);

        return find(id);
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM time_entries WHERE id = ?", id);
    }

    @Override
    public List<TimeEntry> list() {

        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "SELECT * FROM time_entries");

        List<TimeEntry> timeEntries = new ArrayList<>();

        for(Map<String, Object> map: list){
            TimeEntry timeEntry = new TimeEntry();
            timeEntry.setId((long)map.get("id"));
            timeEntry.setProjectId((long)map.get("project_id"));
            timeEntry.setUserId((long)map.get("user_id"));
            timeEntry.setDate(((java.sql.Date)map.get("date")).toLocalDate());
            timeEntry.setHours((int)map.get("hours"));
            timeEntries.add(timeEntry);
        }

        return timeEntries;
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
}
