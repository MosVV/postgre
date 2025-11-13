package com.example.postgre;


import com.example.postgre.user.dto.request.RegistrationRequest;
import com.example.postgre.user.dto.request.EditUserRequest;
import com.example.postgre.user.dto.response.UserResponse;
import com.example.postgre.user.entity.UserEntity;
import com.example.postgre.user.repository.UserRepository;
import com.example.postgre.user.routeres.UserRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.accept.ContentNegotiationManager;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

public class WebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${init.email}")
    private String initUser;
    @Value("${init.password}")
    private String initPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void config() {
        Optional<UserEntity> check = userRepository.findByEmail(initUser);
        if (check.isPresent()) return;

        UserEntity user = UserEntity.builder()
                .firstName("")
                .lastName("")
                .email(initUser)
                .password(passwordEncoder.encode(initPassword))
                .build();

        userRepository.save(user);
    }

    public String aufHeader() {
        return "Basic " + Base64.getEncoder().encodeToString((initUser + ":" + initPassword).getBytes());
    }


    @Test
    void contextLoad() throws Exception {
        UserEntity user = UserEntity.builder()
                .firstName("1")
                .lastName("1")
                .build();

        user = userRepository.save(user);

        mockMvc.perform(get(UserRoutes.BY_ID, user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, aufHeader())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createTest() throws Exception {
        RegistrationRequest request = RegistrationRequest.builder()
                .firstName("createTest")
                .lastName("createTest")
                .email("eee@ee.ru")
                .password("1")
                .build();

        mockMvc.perform(
                        post(UserRoutes.REGISTRATION)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(content().string(containsString("createTest")));

    }

    @Test
    void findByIdTest() throws Exception {
        UserEntity user = UserEntity.builder()
                .lastName("findById")
                .firstName("findById")
                .build();
        userRepository.save(user);

        mockMvc.perform(get(UserRoutes.BY_ID, user.getId()).contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, aufHeader())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("findById")));

    }

    @Test
    void findById_NotFound_Test() throws Throwable {
        mockMvc.perform(get(UserRoutes.BY_ID, 10000).contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, aufHeader()))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    void updateTest() throws Exception {
        UserEntity user = UserEntity.builder()
                .firstName("up")
                .lastName("up")
                .build();
        userRepository.save(user);
        EditUserRequest request = EditUserRequest.builder()
                .firstName("updateTest")
                .lastName("updateTest")
                .build();
        mockMvc.perform(put(UserRoutes.EDIT, user.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(HttpHeaders.AUTHORIZATION, aufHeader())
        ).andDo(print()).andExpect(content().string(containsString("updateTest")));

    }

    @Test
    void deleteTest() throws Exception {
        UserEntity user = UserEntity.builder()
                .firstName("updateTest")
                .lastName("updateTest")
                .build();
        user = userRepository.save(user);

        mockMvc.perform(delete(UserRoutes.BY_ID, user.getId())
                        .header(HttpHeaders.AUTHORIZATION, aufHeader()))
                .andDo(print()).andExpect(status().isOk());

        assert userRepository.findById(user.getId()).isEmpty();
    }

    @Test
    void searchTest() throws Exception {
        List<UserResponse> result = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            UserEntity user = UserEntity.builder()
                    .firstName("firstName_" + i)
                    .lastName("lastName_" + i)
                    .build();
            user = userRepository.save(user);
            result.add(UserResponse.of(user));
        }

        mockMvc.perform(get(UserRoutes.SEARCH)
                        .param("size", "1000")
                        .param("query","firstName_")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, aufHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));

    }
}
