package com.example.postgre;

import com.example.postgre.user.entity.UserEntity;
import com.example.postgre.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PostgresApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void repositoryTest() {
        UserEntity user = UserEntity.builder()
                .lastName("11")
                .firstName("11")
                .build();
        user = userRepository.save(user);

        UserEntity check=userRepository.findById(user.getId()).get();

        assert check.getId().equals(user.getId());

    }

}
