package com.example.postgre.user.controller;


import com.example.postgre.user.EditUserRequest;
import com.example.postgre.user.dto.request.CreateUserRequest;
import com.example.postgre.user.dto.response.UserResponse;
import com.example.postgre.user.exception.BadRequestException;
import com.example.postgre.user.exception.UserNotFoundException;
import com.example.postgre.user.repository.UserRepository;
import com.example.postgre.user.entity.UserEntity;
import com.example.postgre.user.routeres.UserRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserApiController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${init.email}")
    private String initUser;
    @Value("${init.password}")
    private String initPassword;

    @GetMapping("/")
    public UserEntity root() {
        UserEntity user = UserEntity.builder()
                .firstName("test")
                .lastName("Test")
                .build();
        user = userRepository.save(user);
        return user;
    }

    @PostMapping(UserRoutes.CREATE)
    public UserResponse create(@RequestBody CreateUserRequest request) throws BadRequestException {
        request.validate();

        UserEntity user = UserEntity.builder()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        user = userRepository.save(user);
        return UserResponse.of(user);
    }

    @GetMapping(UserRoutes.BY_ID)
    public UserResponse byId(@PathVariable Long id) throws UserNotFoundException {
        return UserResponse.of(userRepository.findById(id).orElseThrow(UserNotFoundException::new));

    }

    @GetMapping(UserRoutes.SEARCH)
    public List<UserResponse> search(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).stream().map(UserResponse::of).collect(Collectors.toList());
    }

    @Operation(summary = "Редактирование пользователя", description = "Редактироване пользователя, который существует в базе")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успешно отредактированный пользователь",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Не корректный запрос",
                            content = @Content),
                    @ApiResponse(responseCode = "404", description = "Пользователь с таким id не найден",
                            content = @Content)
            }


    )


    @PutMapping(UserRoutes.BY_ID)
    public UserResponse edit(@PathVariable Long id, @RequestBody EditUserRequest request) throws UserNotFoundException {
        UserEntity user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        user.setLastName(request.getLastName());
        user.setFirstName(request.getFirstName());
        user = userRepository.save(user);

        return UserResponse.of(user);
    }

    @DeleteMapping(UserRoutes.BY_ID)
    public String delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return HttpStatus.OK.name();
    }

    @GetMapping(UserRoutes.INIT)
    public UserResponse init() {
        Optional<UserEntity> checkUser = userRepository.findByEmail(initUser);
        UserEntity user;
        if (checkUser.isEmpty()) {
            user = UserEntity.builder()
                    .firstName("Default")
                    .lastName("Default")
                    .email(initUser)
                    .password(passwordEncoder.encode(initPassword))
                    .build();
            user = userRepository.save(user);
        } else {
            user = checkUser.get();
        }
        return UserResponse.of(user);
    }
}
