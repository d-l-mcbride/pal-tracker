package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;


@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {


    TimeEntryRepository timeEntryRepository;
    private final DistributionSummary timeEntrySummary;
    private final Counter actionCounter;


    public TimeEntryController(TimeEntryRepository timeEntryRepository, MeterRegistry meterRegistry){

        this.timeEntryRepository = timeEntryRepository;
        timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        actionCounter = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntryToCreate) {

        TimeEntry timeEntry = timeEntryRepository.create(timeEntryToCreate);
        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());
        return ResponseEntity.status(HttpStatus.CREATED).body(timeEntry);
    }

    @GetMapping("/{timeEntryId}")
    public ResponseEntity<TimeEntry> read(@PathVariable long timeEntryId) {

        TimeEntry timeEntry =timeEntryRepository.find(timeEntryId);

        if(timeEntry == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        actionCounter.increment();
        return ResponseEntity.ok().body(timeEntry);
    }


    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        List<TimeEntry> timeEntries = timeEntryRepository.list();
        actionCounter.increment();
        return ResponseEntity.ok().body(timeEntries);
    }


    @PutMapping("/{timeEntryId}")
    public ResponseEntity update(@PathVariable long timeEntryId,@RequestBody TimeEntry expected) {

        TimeEntry timeEntry = timeEntryRepository.update(timeEntryId, expected);
        if(timeEntry == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        actionCounter.increment();
        return ResponseEntity.ok().body(timeEntry);
    }


    @DeleteMapping("/{timeEntryId}")
    public ResponseEntity delete(@PathVariable long timeEntryId) {

        timeEntryRepository.delete(timeEntryId);
        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
