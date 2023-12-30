package com.SCU.pose.repository;

import com.SCU.pose.model.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Integer> {

    /**
     * Finds exercise records for a specific user within a specified date range, ordered by exercise date.

     *
     * @param userId     The ID of the user.
     * @param startDate  The start date of the range.
     * @param endDate    The end date of the range.
     * @return A list of ExerciseRecord objects, ordered by exercise date.
     */
    List<ExerciseRecord> findByUserIdAndExerciseDateBetweenOrderByExerciseDateAsc(
            int userId, Date startDate, Date endDate);

    /**
     * Finds exercise records for a specific user and exercise type within a specified date range, ordered by exercise date.

     *
     * @param userId       The ID of the user.
     * @param exerciseType The type of the exercise (e.g., "push_up", "plank").
     * @param startDate    The start date of the range.
     * @param endDate      The end date of the range.
     * @return A list of ExerciseRecord objects, ordered by exercise date.
     */
    List<ExerciseRecord> findByUserIdAndExerciseTypeAndExerciseDateBetweenOrderByExerciseDateAsc(
            int userId, String exerciseType, Date startDate, Date endDate);
}
