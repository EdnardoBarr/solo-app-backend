package ednardo.api.soloapp.config;

import ednardo.api.soloapp.filter.CorsFilter;
import ednardo.api.soloapp.filter.UserAuthenticationFilter;
import ednardo.api.soloapp.service.impl.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserAuthenticationFilter userAuthenticationFilter;

    @Autowired
    private AuthEntryPoint authEntryPoint;

    @Value("${cors.filter.apply}")
    private Boolean applyCorsFilter;


    public static final String[] ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED = {
            "/login/**",
            "/user/**",
            "/swagger-ui/**",
            "/test"
    };

    public static final String[] ENDPOINTS_WITH_AUTHENTICATION_REQUIRED = {
            "/users/test/user"
    };

    public static final String[] ENDPOINTS_USER = {
            "/users/test/customer"
    };

    public static final String[] ENDPOINTS_ADMIN = {
            "/users/test/administrator"
    };

    public static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/login",
            "/api/v1/hello/**",
            "/api/v1/user/hello",
            "/api/v1/user/login",
          //  "/api/v1/user/logged",
            "/api/v1/role/**",
            "/api/v1/user/register",
            "/api/v1/user/refresh-token",
    };

    @Bean
    CorsFilter corsFilter() {
        return new CorsFilter(this.applyCorsFilter);
    }

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public UserAuthenticationFilter authenticationJwtTokenFilter() {
        return new UserAuthenticationFilter();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addExposedHeader("total-size");
        configuration.setExposedHeaders(List.of("total-size"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().
                exceptionHandling().authenticationEntryPoint(this.authEntryPoint).and().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests().
                requestMatchers(SWAGGER_WHITELIST).permitAll().
                requestMatchers(HttpMethod.OPTIONS, SWAGGER_WHITELIST).permitAll().
                anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(corsFilter(), BasicAuthenticationFilter.class);

        return http.build();
//        http
//                .csrf().disable()
//                .exceptionHandling().authenticationEntryPoint(this.authEntryPoint).and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
//                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                        .anyRequest().authenticated()
//                        .and().addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
//                )
//                .formLogin(withDefaults());
             //   .httpBasic(withDefaults());

     //   http.addFilterAfter(corsFilter(), BasicAuthenticationFilter.class);
//         http.csrf().disable() // Desativa a proteção contra CSRF
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Configura a política de criação de sessão como stateless
//                .and().authorizeHttpRequests() // Habilita a autorização para as requisições HTTP
//                .requestMatchers(SWAGGER_WHITELIST).permitAll()
//                .anyRequest().authenticated()
//                .anyRequest().denyAll();
        // Adiciona o filtro de autenticação de usuário que criamos, antes do filtro de segurança padrão do Spring Security
        // .and().addFilterBefore(userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

      //  return http.getOrBuild();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers("/ignore1", "/ignore2");
//    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authProvider())
                .build();
    }

}
