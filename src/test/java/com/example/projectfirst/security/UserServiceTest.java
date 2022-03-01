package com.example.projectfirst.security;

import com.example.projectfirst.security.exceptions.APIPBadCredentialsException;
import com.example.projectfirst.security.exceptions.APIPUserAlreadyExists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService underTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private MyUserDetailsService myUserDetailsService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository, jwtUtil, authenticationManager, myUserDetailsService, passwordEncoder);
    }

    @Test
    void saveUser() {
        String username = "usernameTest";
        MyUser myUser = new MyUser("test-id", "name", "surname", username, "pass");

        given(userRepository.existsByUsername(any())).willReturn(false);

        String capturedOutput = underTest.saveUser(myUser);
        String expectedOutput = "Successfully signed in!";

        myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));
        ArgumentCaptor<MyUser> userArgumentCaptor = ArgumentCaptor.forClass(MyUser.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        MyUser capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(myUser);
        assertThat(capturedOutput).isEqualTo(expectedOutput);
    }

    @Test
    void willThrowWhenUserAlreadyExists() {
        given(userRepository.existsByUsername(any())).willReturn(true);

        assertThatThrownBy(() -> underTest.saveUser(new MyUser()))
                .isInstanceOf(APIPUserAlreadyExists.class)
                .hasMessageContaining("User with this username already exists!");
    }

    @Test
    void getTokenForUser() {
        JWTRequest jwtRequest = new JWTRequest("username", "pass");

        UsernamePasswordAuthenticationToken expectedAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                                                    jwtRequest.getUsername(),
                                                                    jwtRequest.getPassword());

        User user = new User(jwtRequest.getUsername(), jwtRequest.getPassword(), new ArrayList<>());
        given(myUserDetailsService.loadUserByUsername(any())).willReturn(user);

        String expectedToken = "some-token";
        given(jwtUtil.generateToken(any())).willReturn(expectedToken);

        JWTResponse expectedJwtResponse = new JWTResponse(expectedToken);

        JWTResponse jwtResponse = underTest.getTokenForUser(jwtRequest);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenArgumentCaptor
                = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(tokenArgumentCaptor.capture());

        UsernamePasswordAuthenticationToken authenticationToken = tokenArgumentCaptor.getValue();

        assertThat(authenticationToken).isEqualTo(expectedAuthenticationToken);
        assertThat(jwtResponse).isEqualTo(expectedJwtResponse);
    }

    @Test
    void willThrowWhenBadCredentials() {
        JWTRequest jwtRequest = new JWTRequest("username", "pass");

        given(authenticationManager.authenticate(any())).willThrow(BadCredentialsException.class);

        assertThatThrownBy(() -> underTest.getTokenForUser(jwtRequest))
                .isInstanceOf(APIPBadCredentialsException.class)
                .hasMessageContaining("Incorrect username or password!");
    }
}