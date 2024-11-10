package com.capgemini.wsb.fitnesstracker.training.api;

import java.util.Optional;

public interface TrainingService {

    /**
     * Creates a new training.
     *
     * @param training The training object to create.
     * @return The created training.
     */
    Training createTraining(Training training);

    /**
     * Updates an existing training. Allows updating selected fields of the training.
     *
     * @param trainingId     ID of the training to update.
     * @param updatedTraining The training object containing updated data.
     * @return An {@link Optional} containing the updated training or {@link Optional#empty()} if not found.
     */
    Training updateTraining(Long trainingId, TrainingDto updatedTraining);

    /**
     * Deletes a training based on its ID.
     *
     * @param trainingId ID of the training to delete.
     */
    void deleteTraining(Long trainingId);
}
