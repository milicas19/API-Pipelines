package com.example.projectfirst.security;

import com.example.projectfirst.security.exceptions.APIPBadCredentialsException;
import com.example.projectfirst.security.exceptions.APIPUserAlreadyExists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String saveUser(MyUser user) {
        log.info("Saving user!");
        if(userRepository.existsByUsername(user.getUsername())){
            log.error("User with this username already exists!");
            throw new APIPUserAlreadyExists("User with this username already exists!");
        }
        else{
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            log.info("Successfully signed in!");
            return "Successfully signed in!";
        }
    }

    public JWTResponse getTokenForUser(JWTRequest jwtRequest) throws APIPBadCredentialsException{
        log.info("Getting token for user!");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            jwtRequest.getUsername(),
                            jwtRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            log.error("Incorrect username or password!");
            throw new APIPBadCredentialsException("Incorrect username or password!");
        }

        final UserDetails userDetails
                = myUserDetailsService.loadUserByUsername(jwtRequest.getUsername());

        final String token =
                jwtUtil.generateToken(userDetails);

        log.info("Token successfully obtained!");
        return  new JWTResponse(token);
    }
}
