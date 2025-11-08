package com.example.postgre.user.controller;


import com.example.postgre.user.EditUserRequest;
import com.example.postgre.user.dto.request.CreateUserRequest;
import com.example.postgre.user.dto.response.UserResponse;
import com.example.postgre.user.exception.UserNotFoundException;
import com.example.postgre.user.repository.UserRepository;
import com.example.postgre.user.entity.UserEntity;
import com.example.postgre.user.routeres.UserRoutes;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class UserApiController {
    private final UserRepository userRepository;

    @GetMapping("/")
    public UserEntity test() {
        UserEntity user = UserEntity.builder()
                .firstName("test")
                .lastName("Test")
                .build();
        user = userRepository.save(user);
        return user;
    }

    @PostMapping(UserRoutes.CREATE)
    public UserResponse create(@RequestBody CreateUserRequest request) {
        UserEntity user = UserEntity.builder()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
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
    ){
        Pageable pageable = PageRequest.of(page,size);
        return userRepository.findAll(pageable).stream().map(UserResponse::of).collect(Collectors.toList());
    }

    @PutMapping(UserRoutes.BY_ID)
    public UserResponse edit(@PathVariable Long id, @RequestBody EditUserRequest request)throws UserNotFoundException{
        UserEntity user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        user.setLastName(request.getLastName());
        user.setFirstName(request.getFirstName());
        user=userRepository.save(user);

        return UserResponse.of(user);
    }

    @DeleteMapping(UserRoutes.BY_ID)
    public String delete(@PathVariable Long id){
        userRepository.deleteById(id);
        return HttpStatus.OK.name();
    }
}
