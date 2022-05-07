package com.todolist.backend.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.todolist.backend.entity.*;
import com.todolist.backend.security.util.JwtService;
import com.todolist.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
public class TokenController {

    private final UserService userService;

    private final JwtService jwtService;

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest request) {

        Map<String, Object> bodyResponse = new HashMap<>();

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                DecodedJWT decodedJWT = jwtService.decodeJwt(refreshToken);
                String userId = decodedJWT.getSubject();
                User user = userService.getById(Integer.parseInt(userId));

                String newAccessToken = jwtService.createAccessToken(userId, user.getRoles()
                        .stream().map(Role::getName).collect(Collectors.toList()));
                String newRefreshToken = jwtService.createRefreshToken(userId);

                bodyResponse.put("accessToken", newAccessToken);
                bodyResponse.put("refreshToken", newRefreshToken);
                return new ResponseEntity<>(bodyResponse, HttpStatus.OK);

            } catch (Exception e) {
                bodyResponse.put("errorMessage", e.getMessage());
                return new ResponseEntity<>(bodyResponse, HttpStatus.FORBIDDEN);
            }
        } else {
            bodyResponse.put("errorMessage", "Refresh token is missing");
            return new ResponseEntity<>(bodyResponse, HttpStatus.FORBIDDEN);
        }
    }
}
