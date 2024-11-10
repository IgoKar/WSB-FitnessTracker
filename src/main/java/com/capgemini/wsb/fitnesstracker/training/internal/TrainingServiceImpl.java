package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.*;
import com.capgemini.wsb.fitnesstracker.user.api.UserNotFoundException;
import com.capgemini.wsb.fitnesstracker.user.internal.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements TrainingProvider, TrainingService {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves a training based on its ID.
     * If the training with the given ID is not found, a {@link TrainingNotFoundException} is thrown.
     *
     * @param trainingId ID of the training to retrieve.
     * @return An {@link Optional} containing the located training, or {@link Optional#empty()} if not found.
     */
    @Override
    public Optional<Training> getTraining(Long trainingId) {
        return trainingRepository.findById(trainingId);
    }

    /**
     * Deletes a training based on its ID.
     * If the training with the given ID does not exist, a {@link TrainingNotFoundException} will be thrown.
     *
     * @param trainingId ID of the training to delete.
     */
    @Override
    public void deleteTraining(Long trainingId) {
        if (!trainingRepository.existsById(trainingId)) {
            throw new TrainingNotFoundException(trainingId);
        }
        trainingRepository.deleteById(trainingId);
    }

    /**
     * Returns a list of trainings for a specific activity type (e.g., running, cycling).
     *
     * @param activityType The type of activity for which to retrieve trainings.
     * @return List of trainings with the specified activity type.
     */
    @Override
    public List<Training> findAllTrainingsByActivityType(ActivityType activityType) {
        return trainingRepository.findByActivityType(activityType);
    }

    /**
     * Returns a list of all available trainings.
     *
     * @return List of all trainings.
     */
    @Override
    public List<Training> findAllTrainings() {
        return trainingRepository.findAll();
    }

    /**
     * Returns a list of finished trainings that occurred before a specified time.
     *
     * @param afterTime Date after which finished trainings are to be retrieved.
     * @return List of finished trainings after the specified date.
     */
    @Override
    public List<Training> findAllFinishedTrainingsAfterTime(LocalDateTime afterTime) {
        return trainingRepository.findByEndTimeBefore(afterTime);
    }

    /**
     * Creates a new training.
     * If the user is null or not found, a {@link UserNotFoundException} is thrown.
     *
     * @param training The training object to create.
     * @return The created training.
     * @throws UserNotFoundException if the user associated with the training does not exist.
     */
    @Override
    public Training createTraining(Training training) {
        log.info("Creating Training: {}", training);

        if (training.getUser() == null) {
            throw new UserNotFoundException("User doesn't exist");
        }
        if (!userRepository.existsById(training.getUser().getId())) {
            throw new UserNotFoundException("User with ID " + training.getUser().getId() + " not found");
        }

        return trainingRepository.save(training);
    }

    /**
     * Updates an existing training. Not every param is possible to update.
     * If the training with the given ID does not exist, a {@link TrainingNotFoundException} is thrown.
     * If the user does not exist, a {@link UserNotFoundException} is thrown.
     *
     * @param trainingId      ID of the training to update.
     * @param updatedTraining The training object containing updated data.
     * @return The updated training.
     * @throws TrainingNotFoundException if the training with the provided ID is not found.
     * @throws UserNotFoundException     if the user associated with the training is not found.
     */
    @Override
    public Training updateTraining(Long trainingId, TrainingDto updatedTraining) {
        Training trainingToUpdate = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        if (trainingToUpdate.getUser() == null) {
            throw new UserNotFoundException("User doesn't exist");
        }
        if (!userRepository.existsById(trainingToUpdate.getUser().getId())) {
            throw new UserNotFoundException("User with ID " + trainingToUpdate.getUser().getId() + " not found");
        }

        if (updatedTraining.startTime() != null) {
            trainingToUpdate.setStartTime(updatedTraining.startTime());
        }
        if (updatedTraining.endTime() != null) {
            trainingToUpdate.setEndTime(updatedTraining.endTime());
        }
        if (updatedTraining.activityType() != null) {
            trainingToUpdate.setActivityType(updatedTraining.activityType());
        }
        if (updatedTraining.distance() >= 0) {
            trainingToUpdate.setDistance(updatedTraining.distance());
        }
        if (updatedTraining.averageSpeed() >= 0) {
            trainingToUpdate.setAverageSpeed(updatedTraining.averageSpeed());
        }


        return trainingRepository.save(trainingToUpdate);
    }
}
