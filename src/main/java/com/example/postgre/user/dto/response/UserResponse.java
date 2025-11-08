package com.example.postgre.user.dto.response;


import com.example.postgre.user.entity.UserEntity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserResponse extends UserEntity {
    protected Long id;
    protected String firstName;
    protected String lastName;
    public static UserResponse of(UserEntity user){
        return UserResponse.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .build();
    }
}
