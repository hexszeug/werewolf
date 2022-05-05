package eu.hexsz.werewolf.controller;

public class NightController {
    public void manageNight(Job job) {
        if (job == null) {
            return;
        }
        job.done();
    }
}
