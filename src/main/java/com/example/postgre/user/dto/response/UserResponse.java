package com.example.postgre.user.dto.response;


import com.example.postgre.user.entity.UserEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserResponse  {
    protected Long id;
    protected String firstName;
    protected String lastName;
    protected String email;

    public static UserResponse of(UserEntity user){
        return UserResponse.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .build();
    }
}
