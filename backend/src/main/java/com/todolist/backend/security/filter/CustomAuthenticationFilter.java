package com.todolist.backend.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.*;
import com.todolist.backend.security.util.*;
import org.springframework.security.authentication.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.*;
import javax.servlet.FilterChain;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        CustomUser user = (CustomUser) authentication.getPrincipal();
        String accessToken = jwtService.createAccessToken(Integer.toString(user.getId()), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        String refreshToken = jwtService.createRefreshToken(Integer.toString(user.getId()));
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        jsonMapper.writeValue(response.getOutputStream(), tokens);
    }
}
