package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingDto;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.internal.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    private final UserMapper userMapper;

    @Autowired
    public TrainingMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    /**
     * Converts a Training entity to a TrainingDto.
     *
     * @param training the Training entity to convert.
     * @return a TrainingDto representing the given Training entity.
     */
    public TrainingDto toDto(Training training) {
        if (training == null) {
            return null;
        }

        return new TrainingDto(
                training.getId(),
                userMapper.toDto(training.getUser()),
                training.getStartTime(),
                training.getEndTime(),
                training.getActivityType(),
                training.getDistance(),
                training.getAverageSpeed());
    }

    /**
     * Converts a TrainingDto to a Training entity.
     *
     * @param trainingDto the TrainingDto to convert.
     * @return a Training entity representing the given TrainingDto.
     */
    public Training toEntity(TrainingDto trainingDto) {
        if (trainingDto == null) {
            return null;
        }

        User user = userMapper.toEntity(trainingDto.user());

        return new Training(
                user,
                trainingDto.startTime(),
                trainingDto.endTime(),
                trainingDto.activityType(),
                trainingDto.distance(),
                trainingDto.averageSpeed());

    }
}
