package com.example.projectfirst.security;

import com.example.projectfirst.security.exceptions.APIPUserNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    private MyUserDetailsService underTest;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        underTest = new MyUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername() {
        String username = "usernameTest";
        MyUser myUser = new MyUser("test-id", "name", "surname", username, "pass");

        given(userRepository.findByUsername(any())).willReturn(Optional.of(myUser));

        User expectedUser = new User(myUser.getUsername(), myUser.getPassword(), new ArrayList<>());

        UserDetails user = underTest.loadUserByUsername(username);

        assertThat(user).isEqualTo(expectedUser);
    }

    @Test
    void willThrowWhenUserNotFound() {
        String username = "usernameTest";

        given(userRepository.findByUsername(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.loadUserByUsername(username))
                .isInstanceOf(APIPUserNotFound.class)
                .hasMessageContaining("User with this username does not exists!");
    }
}