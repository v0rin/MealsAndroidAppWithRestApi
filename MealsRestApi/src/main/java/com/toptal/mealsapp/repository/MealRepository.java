package com.toptal.mealsapp.repository;

import com.toptal.mealsapp.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    @Query(nativeQuery=true, value=
            "select m1.id as id, " +
                    "m1.date as date, " +
                    "m1.time as time, " +
                    "m1.description as description, " +
                    "m1.calories as calories, " +
                    "m1.user_id as userId, " +
                    "m3.calories_for_the_day as caloriesForTheDay " +
            "from meal m1 " +
                    "inner join " +
                        "(select m2.date, sum(m2.calories) as calories_for_the_day " +
                        "from meal m2 " +
                        "where m2.user_id=?1 " +
                        "group by m2.date) m3 " +
                    "on m1.date=m3.date " +
            "where m1.user_id=?1 " +
            "order by m1.date desc, m1.time asc")
    List<MealWithCaloriesForTheDay> findByUserIdWithDailyCalories(Long userId);

    @Query(nativeQuery=true, value=
            "select m1.id as id, " +
                    "m1.date as date, " +
                    "m1.time as time, " +
                    "m1.description as description, " +
                    "m1.calories as calories, " +
                    "m1.user_id as userId, " +
                    "m3.calories_for_the_day as caloriesForTheDay " +
            "from meal m1 " +
                    "inner join " +
                        "(select m2.date, sum(m2.calories) as calories_for_the_day " +
                        "from meal m2 " +
                        "where m2.user_id=?1 and m2.date between ?2 and ?3 and m2.time between ?4 and ?5 " +
                        "group by date) m3 " +
                    "on m1.date=m3.date " +
            "where m1.user_id=?1 and m1.date between ?2 and ?3 and m1.time between ?4 and ?5 " +
            "order by m1.date desc, m1.time asc")
    List<MealWithCaloriesForTheDay> findByUserIdWithDailyCaloriesBetweenDatesAndTimes(Long userId,
                                                                                      String startDate,
                                                                                      String endDate,
                                                                                      String startTime,
                                                                                      String endTime);

    @Query(value = "from Meal m where date BETWEEN :startDate AND :endDate AND time BETWEEN :startTime AND :endTime")
    List<Meal> getAllBetweenDates(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("startTime") LocalTime startTime,
                                         @Param("startTime") LocalTime endTime);


    List<Meal> findByUserIdOrderByDateDescTimeAsc(Long userId);


    interface MealWithCaloriesForTheDay {
        Long getId();
        LocalDate getDate();
        LocalTime getTime();
        String getDescription();
        Double getCalories();
        Long getUserId();
        Double getCaloriesForTheDay();
    }

}
