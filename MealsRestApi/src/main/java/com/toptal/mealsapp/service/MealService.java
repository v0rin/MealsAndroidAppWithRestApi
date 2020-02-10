package com.toptal.mealsapp.service;

import com.toptal.mealsapp.model.Meal;
import com.toptal.mealsapp.model.User;
import com.toptal.mealsapp.model.dto.MealDto;
import com.toptal.mealsapp.repository.MealRepository;
import com.toptal.mealsapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;


@Service
public class MealService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final Function<Meal, MealDto> entityToDtoConverter =
            entity -> new MealDto(entity.getId(), entity.getDate(), entity.getTime(), entity.getDescription(), entity.getCalories(), entity.getUser().getId(), null);

    @Autowired
    private MealRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


    public List<MealDto> findAllForCurrentUser() {
        User currUser = userService.getCurrentUser();
        return findAllByUserId(currUser.getId());
    }


    public List<MealDto> findAllByUserId(Long userId) {
        List<MealDto> list = repository.findByUserIdWithDailyCalories(userId).stream()
                .map(m -> new MealDto(m.getId(), m.getDate(), m.getTime(), m.getDescription(), m.getCalories(), m.getUserId(), m.getCaloriesForTheDay()))
                .collect(toList());
        return list;
    }


    public List<MealDto> getFilteredByDateAndTimeForCurrentUser(LocalDate startDate,
                                                                LocalDate endDate,
                                                                LocalTime startTime,
                                                                LocalTime endTime) {
        return getFilteredByDateAndTimeByUserId(userService.getCurrentUser().getId(), startDate, endDate, startTime, endTime);
    }


    public List<MealDto> getFilteredByDateAndTimeByUserId(Long userId,
                                                          LocalDate startDate,
                                                          LocalDate endDate,
                                                          LocalTime startTime,
                                                          LocalTime endTime) {
        List<MealDto> list = repository.findByUserIdWithDailyCaloriesBetweenDatesAndTimes(
                    userId,
                    DATE_FORMATTER.format(startDate),
                    DATE_FORMATTER.format(endDate),
                    TIME_FORMATTER.format(startTime),
                    TIME_FORMATTER.format(endTime))
                .stream()
                .map(m -> new MealDto(m.getId(), m.getDate(), m.getTime(), m.getDescription(), m.getCalories(), m.getUserId(), m.getCaloriesForTheDay()))
                .collect(toList());
        return list;
    }


    public Long create(MealDto dto) {
        User user;
        if (dto.getUserId() != null && userService.isCurrentUserAdmin()) {
            user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException(format("User with id=[%s] not found", dto.getUserId())));
        }
        else {
            user = userService.getCurrentUser();
        }
        Meal newEntity = new Meal(dto.getDate(), dto.getTime(), dto.getDescription(), dto.getCalories(), user);
        return repository.save(newEntity).getId();
    }


    public void update(MealDto dto, Long id) {
        Meal entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(format("Meal with id=[%s] not found", id)));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(format("User with id=[%s] not found", dto.getUserId())));

        User currUser = userService.getCurrentUser();

        if (user.getId().equals(currUser.getId()) || userService.isCurrentUserAdmin()) {
            Meal updatedEntity = new Meal(dto.getDate(), dto.getTime(), dto.getDescription(), dto.getCalories(), user);
            updatedEntity.setId(id);
            repository.save(updatedEntity);
        }
    }


    public void deleteById(Long id) {
        User currUser = userService.getCurrentUser();
        Meal entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(format("Meal with id=[%s] not found", id)));

        if (entity.getUser().getId().equals(currUser.getId()) || userService.isCurrentUserAdmin()) {
            repository.deleteById(id);
        }
    }

}
