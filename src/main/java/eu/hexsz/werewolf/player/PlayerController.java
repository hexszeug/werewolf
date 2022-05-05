package eu.hexsz.werewolf.player;

import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.api.RequestHandler;
import eu.hexsz.werewolf.controller.Job;
import eu.hexsz.werewolf.controller.NightController;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Must be implemented by each role. The class of the {@code PlayerController}
 * stored in the corresponding {@link Player} determines which role the player has.
 * All specific role behavior should be in the implementation of this class for each role.
 * For more information about how to implement custom behavior see the method docs of this class.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@AllArgsConstructor
public abstract class PlayerController implements RequestHandler {
    private @Getter Player player;

    //dependencies
    NightController nightController;

    /**
     * Is called by {@link NightController} to remind the {@code PlayerController}
     * to set all alarm for the next night.
     * @see NightController
     * @since 1.0-SNAPSHOT
     * */
    public abstract void setAlarms();

    /**
     * Is called by {@link NightController} when the {@code PlayerController} was chosen
     * to manage a night phase. Calls {@link Job#done()} of the passed job
     * to tell the {@code NightController} to end the phase.
     * <br>The {@code NightController} awakes the {@link Player}s and tells them to fall asleep again,
     * so the {@code PlayerController} shouldn't do this.
     * @param job The job to close when finished the asynchronous task.
     * @since 1.0-SNAPSHOT
     * */
    public abstract void manageNightPhase(Job job);

    /**
     * Is called by {@link eu.hexsz.werewolf.controller.ExecutionService} when the player of this
     * {@code PlayerController} was killed. Should set the status of the {@link Player} to
     * {@link Status#DEAD} at first unless a special ability of the role allows to survive a kill.
     * After can happen anything else role specific which is triggered by the death of the player.
     * @param job The job to close when finished the asynchronous task.
     * @since 1.0-SNAPSHOT
     * */
    public void lastWords(Job job) {
        if (job == null) {
            return;
        }
        player.setStatus(Status.DEAD);
        job.done();
    }

    /**
     * {@inheritDoc}
     *
     * @param request*/
    public final void receive(Request request) {
        if (request == null) {
            return;
        }
        switch (request.getType()) {
            //TODO handle day actions
            default: handleRoleSpecificRequest(request);
        }
    }

    /**
     * Contains the request handling for role specific requests.
     * Should not be called by anyone else than {@link RequestHandler#receive(Request)}
     * @param request The request to handle
     * @since 1.0-SNAPSHOT
     * */
    protected abstract void handleRoleSpecificRequest(Request request);
}
