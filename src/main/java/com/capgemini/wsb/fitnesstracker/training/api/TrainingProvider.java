package com.capgemini.wsb.fitnesstracker.training.api;

import com.capgemini.wsb.fitnesstracker.training.internal.ActivityType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface defining CRUD operations for training management.
 */
public interface TrainingProvider {

    /**
     * Retrieves a training based on its ID.
     * If the training with the given ID is not found, {@link Optional#empty()} is returned.
     *
     * @param trainingId ID of the training to retrieve.
     * @return An {@link Optional} containing the located training or {@link Optional#empty()} if not found.
     */
    Optional<Training> getTraining(Long trainingId);

    /**
     * Returns a list of all available trainings.
     *
     * @return List of all trainings.
     */
    List<Training> findAllTrainings();

    /**
     * Returns a list of trainings for a specific activity type (e.g., running).
     *
     * @param activityType The type of activity for which to retrieve trainings.
     * @return List of trainings with the specified activity type.
     */
    List<Training> findAllTrainingsByActivityType(ActivityType activityType);

    /**
     * Returns a list of all finished trainings that occurred after a specified date.
     *
     * @param afterTime Date after which finished trainings are to be retrieved.
     * @return List of finished trainings after the specified date.
     */
    List<Training> findAllFinishedTrainingsAfterTime(LocalDateTime afterTime);
}
