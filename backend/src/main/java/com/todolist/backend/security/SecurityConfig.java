package com.todolist.backend.security;

import lombok.RequiredArgsConstructor;
import com.todolist.backend.security.filter.*;
import com.todolist.backend.security.util.JwtService;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(
        securedEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //setAuthorizeRequestsPermitAll(http);
        //setAuthorizeRequestsAdmin(http);
        //setAuthorizeRequestsUser(http);
        //setAuthorizeRequestsAllRoles(http);
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean(), jwtService));
        http.addFilterBefore(new CustomAuthorizationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /*
    private void setAuthorizeRequestsPermitAll(HttpSecurity http) throws Exception{
        http.authorizeRequests().antMatchers(POST, LOGIN, CREATE_USER).permitAll();
        http.authorizeRequests().antMatchers(GET, REFRESH_TOKEN).permitAll();
    }

    private void setAuthorizeRequestsAllRoles(HttpSecurity http) throws Exception{
        String[] allRoles = {ROLE_ADMIN.name(), ROLE_USER.name()};
        http.authorizeRequests().antMatchers(GET, GET_PROFILE).hasAnyAuthority(allRoles);
        http.authorizeRequests().antMatchers(PUT, UPDATE_USER).hasAnyAuthority(allRoles);
        http.authorizeRequests().antMatchers(POST, ADD_PROFILE_PICTURE).hasAnyAuthority(allRoles);
        http.authorizeRequests().antMatchers(DELETE, DELETE_PROFILE_PICTURE).hasAnyAuthority(allRoles);
    }

    private void setAuthorizeRequestsAdmin(HttpSecurity http) throws Exception{
        http.authorizeRequests().antMatchers(GET, GET_USERS, GET_ROLES).hasAnyAuthority(ROLE_ADMIN.name());
        http.authorizeRequests().antMatchers(POST, ADD_ROLE).hasAnyAuthority(ROLE_ADMIN.name());
        http.authorizeRequests().antMatchers(DELETE, REMOVE_ROLE).hasAnyAuthority(ROLE_ADMIN.name());
    }

    private void setAuthorizeRequestsUser(HttpSecurity http) throws Exception{
        http.authorizeRequests().antMatchers(POST, POST_LIST, POST_ITEM).hasAnyAuthority(ROLE_USER.name());
        http.authorizeRequests().antMatchers(GET, GET_LISTS,GET_ITEMS).hasAnyAuthority(ROLE_USER.name());
        http.authorizeRequests().antMatchers(PUT, UPDATE_LIST, UPDATE_ITEM).hasAnyAuthority(ROLE_USER.name());
        http.authorizeRequests().antMatchers(DELETE, DELETE_LIST, DELETE_ITEM).hasAnyAuthority(ROLE_USER.name());
    }*/
}
