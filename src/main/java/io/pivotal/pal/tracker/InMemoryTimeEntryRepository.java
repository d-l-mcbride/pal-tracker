package io.pivotal.pal.tracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {


    Map<Long,TimeEntry> timeEntries = new HashMap<>();
    private long timeId = 0;

    public InMemoryTimeEntryRepository(){

    }


    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        TimeEntry copy = new TimeEntry(++timeId, timeEntry.getProjectId(),timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        timeEntries.put(copy.getId(),copy);
        return copy;
    }

    @Override
    public TimeEntry find(long id) {
        return timeEntries.get(id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        if (timeEntries.get(id)==null){
            return null;
        }

        TimeEntry copy = new TimeEntry(id, timeEntry.getProjectId(),timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        timeEntries.put(id, copy);
        return copy;
    }

    @Override
    public void delete(long id) {

        timeEntries.remove(id);

    }

    @Override
    public List<TimeEntry> list() {
        return timeEntries.values().stream().collect(Collectors.toList());
    }
}
