package ednardo.api.soloapp.config;

import ednardo.api.soloapp.filter.UserAuthenticationFilter;
import ednardo.api.soloapp.model.security.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserAuthenticationFilter userAuthenticationFilter;

    public static final String [] ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED = {
            "/login/**",
            "/user/**",
            "/swagger-ui/**",
            "/test"
    };

    public static final String [] ENDPOINTS_WITH_AUTHENTICATION_REQUIRED = {
            "/users/test/user"
    };

    public static final String [] ENDPOINTS_USER = {
            "/users/test/customer"
    };

    public static final String [] ENDPOINTS_ADMIN = {
            "/users/test/administrator"
    };

    public static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources",
            "/login",
            "/hello/**",
            "/user/login",
            "/user/test",
            "/user/update",
            "/role/create",
            "/role/1",
            "/role/delete/",
            "/user/registration",
            "/swagger-ui/index.html",
            "/swagger-ui.html",
            "/**"
    };
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //    http.csrf().disable().authorizeRequests().requestMatchers(SWAGGER_WHITELIST).permitAll().anyRequest().authenticated().and().httpBasic();
        http
                .csrf().disable().authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SWAGGER_WHITELIST).permitAll().anyRequest().authenticated()
                        .and().addFilterBefore(userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                )
                .formLogin(withDefaults())
                .httpBasic(withDefaults());

//         http.csrf().disable() // Desativa a proteção contra CSRF
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Configura a política de criação de sessão como stateless
//                .and().authorizeHttpRequests() // Habilita a autorização para as requisições HTTP
//                .requestMatchers(SWAGGER_WHITELIST).permitAll()
//                .anyRequest().authenticated()
//                .anyRequest().denyAll();
                // Adiciona o filtro de autenticação de usuário que criamos, antes do filtro de segurança padrão do Spring Security
               // .and().addFilterBefore(userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

         return http.getOrBuild();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers("/ignore1", "/ignore2");
//    }

    @Bean
    public PasswordEncoder encoder(){
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
