package com.toptal.mealsapp.service;

import com.toptal.mealsapp.model.UserRoles;
import com.toptal.mealsapp.model.dto.UserDto;
import com.toptal.mealsapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.toptal.mealsapp.model.User;
import com.toptal.mealsapp.model.UserDetailsImpl;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.toptal.mealsapp.model.UserRoles.ROLE_ADMIN;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Function<UserDto, User> dtoToEntityConverter =
            dto -> new User(dto.getUsername(),
                            dto.getPassword() != null ? passwordEncoder.encode(dto.getPassword()) : null,
                            dto.getRoles(),
                            dto.getMaxDailyCalories());
    private final Function<User, UserDto> entityToDtoConverter =
            entity -> new UserDto(entity.getId(), entity.getUsername(), "", entity.getRoles(), entity.getMaxDailyCalories());


    public List<UserDto> findAll() {
        return repository.findAll().stream().map(entityToDtoConverter::apply).collect(toList());
    }


    public UserDto findById(Long id) {
        return entityToDtoConverter.apply(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(format("User with id=[%s] not found", id))));
    }


    public Long create(UserDto dto) {
        return repository.save(dtoToEntityConverter.apply(dto)).getId();
    }


    public void update(UserDto dto, Long id) {
        User user = repository.findById(id).orElseThrow(() -> new EntityNotFoundException(format("User with id=[%s] not found", id)));
        User entity = dtoToEntityConverter.apply(dto);
        entity.setId(id);
        if (dto.getPassword() == null) {
            // set the current encoded password
            entity.setPassword(user.getPassword());
        }
        repository.save(entity);
    }


    public void deleteById(Long id) {
        repository.deleteById(id);
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = repository.findByUsername(s);
        return new UserDetailsImpl(
                user.orElseThrow(() -> new UsernameNotFoundException(format("User [%s] not found", s))));
    }


    public void updateMaxDailyCalories(Double maxDailyCalories) {
        User currUser = getCurrentUser();
        currUser.setMaxDailyCalories(maxDailyCalories);
        repository.save(currUser);
    }


    public User getCurrentUser() {
        var principal = getCurrentUserDetails();
        return repository.findByUsername(principal.getUsername()).get();
    }

    public UserDetailsImpl getCurrentUserDetails() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


    public boolean isCurrentUserAdmin() {
        return getCurrentUserDetails().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(s -> s.equals(ROLE_ADMIN));
    }

}
