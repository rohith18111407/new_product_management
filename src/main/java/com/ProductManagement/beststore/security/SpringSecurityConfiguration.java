package com.ProductManagement.beststore.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ProductManagement.beststore.models.MyUser;


@Configuration
public class SpringSecurityConfiguration	{
	
	//Autowires a custom success handler for login success.
	@Autowired
	private CustomSuccessHandler customSuccessHandler;
	
	@SuppressWarnings({"removal","deprecation"})
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,JwtRequestFilter jwtRequestFilter) throws Exception {
		
		// Step-1 All request should be authenticated
		http
			.authorizeHttpRequests()
				.requestMatchers(
							"/h2-console/**",
							"/authenticate",
							"/login",
							"/logout",
							"/register",
							"/home",
							"/register",
							"/auth/**"
					).permitAll()

                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
				.requestMatchers("/user/**").hasAuthority("ROLE_USER")
				.anyRequest().authenticated();
//				.anyRequest().permitAll();
						
		
		http
			.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/auth/login")	//attribute used to specify the URL where the application's login form should be submitted for authentication
				.successHandler(customSuccessHandler);

		http
			.logout()
				.logoutUrl("/logout")
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.deleteCookies("JSESSIONID","jwt")
				.logoutSuccessUrl("/login");
		
		// Session will not be created, filterchain should not manage session, session management is stateless
				http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		//Step-3 Disable CSRF -> so that you can send POST, PUT methods
		http.csrf().disable();
		
		http
			.exceptionHandling()
				.authenticationEntryPoint(entryPoint())
				.accessDeniedHandler(accessDeniedHandler());
		
		
			
		http.headers().frameOptions().sameOrigin();	//H2 uses frames but SecurityFilterChain does not allow frames, so this snippet permits
		
		
		
		// Ensures that the H2 database console can be accessed if included in the application
		http.headers().frameOptions().sameOrigin();
		
		
		// insert the custom JwtRequestFilter into the Spring Security filter chain before the UsernamePasswordAuthenticationFilter
		// UsernamePasswordAuthenticationFilter - processing form-based authentication requests
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	

	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
				.build();
	}
	

	
	@Bean
	@Primary
	public UserDetailsService userDetailService(DataSource dataSource) {
		
		var admin=User.withUsername("Rohith")
			.password("pass")
			.passwordEncoder(str->passwordEncoder().encode(str))
			.roles("ADMIN")
			.build();
		
		var jdbcUserDetailsManager=new JdbcUserDetailsManager(dataSource);
		jdbcUserDetailsManager.createUser(admin);		
		return jdbcUserDetailsManager;		
	}
	

	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
		
	@Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, DataSource dataSource) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailService(dataSource)).passwordEncoder(bCryptPasswordEncoder);
        return auth.build();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        return new JwtRequestFilter(userDetailsService, jwtUtil);
    }
    
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomFailureHandler();
    }
    
    @Bean
    public AuthenticationEntryPoint entryPoint() {
        return new JwtAuthenticationEntryPoint();
    }
    	
}
