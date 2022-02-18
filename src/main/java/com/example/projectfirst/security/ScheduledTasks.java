package com.example.projectfirst.security;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    private JWTBlacklistRepository jwtBlacklistRepository;
    @Autowired
    private JWTUtil jwtUtil;

    public final long fixedRate = 1000 * 60 * 10;

    @Scheduled(fixedRate = fixedRate)
    public void updateJwtBlacklist() {
        log.info("Updating jwt blacklist!");
        for (JWT jwt: jwtBlacklistRepository.findAll()) {
            try {
               jwtUtil.isTokenExpired(jwt.getJwtToken());
            }catch (ExpiredJwtException e){
                jwtBlacklistRepository.deleteById(jwt.getId());
            }
        }
    }
}
