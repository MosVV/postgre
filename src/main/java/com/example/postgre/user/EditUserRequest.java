package com.example.postgre.user;

import lombok.Getter;

@Getter
public class EditUserRequest {
    private Long id;
    private  String firstName;
    private  String lastName;
}
