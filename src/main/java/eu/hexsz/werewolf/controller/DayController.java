package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.time.Time;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
//TODO test + implementation
public class DayController {
    //dependencies
    private final Time time;

    public void manageDay(Job job) {
        if (job == null) {
            return;
        }
        while (!time.isNight()) {
            time.nextPhase();
        }
        job.done();
    }
}
