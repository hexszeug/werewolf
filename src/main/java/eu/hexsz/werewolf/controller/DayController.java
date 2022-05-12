package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.time.Time;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DayController {

    private Job job;

    //dependencies
    private final Time time;

    public void manageDay(Job job) {
        if (job == null) {
            return;
        }
        this.job = job;
        while (!time.isNight()) {
            time.nextPhase();
        }
        job.done();
    }
}
