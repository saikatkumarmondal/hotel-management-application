package com.lakesidehotel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lakesidehotel.security.jwt.AuthTokenFilter;
import com.lakesidehotel.security.jwt.JwtAuthEntryPoint;
import com.lakesidehotel.security.user.HotelUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HotelSecurity {
	
	  private final HotelUserDetailsService userDetailsService;
	    private final JwtAuthEntryPoint jwtAuthEntryPoint;

	    @Bean
	    public AuthTokenFilter authenticationTokenFilter(){
	        return new AuthTokenFilter();
	    }
	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	    @Bean
	    public DaoAuthenticationProvider authenticationProvider() {
	        var authProvider = new DaoAuthenticationProvider();
	        authProvider.setUserDetailsService(userDetailsService);
	        authProvider.setPasswordEncoder(passwordEncoder());
	        return authProvider;
	    }

	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
	        return authConfig.getAuthenticationManager();
	    }

	    @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http.csrf(AbstractHttpConfigurer :: disable)
	                .exceptionHandling(
	                        exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
	                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	                .authorizeHttpRequests(auth -> auth
	                        .requestMatchers("/auth/**", "/rooms/**","/bookings/**")
	                        .permitAll().requestMatchers("/roles/**").hasRole("ADMIN")
	                        .anyRequest().authenticated());
	        http.authenticationProvider(authenticationProvider());
	        http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	        return http.build();
	    }

}
