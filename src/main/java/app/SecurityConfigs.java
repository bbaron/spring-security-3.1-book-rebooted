package app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity
public class SecurityConfigs {
    @Bean
    public UserDetailsService userDetailsService() {
        @SuppressWarnings("deprecation")
        var users = User.withDefaultPasswordEncoder();
        var user = users.username("user1@example.com")
                .password("user1")
                .roles("USER")
                .build();
        var admin = users.username("admin1@example.com")
                .password("admin1")
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Configuration
    @Order(1)
    public static class StaticSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/css/**")
                    .authorizeRequests()
                    .anyRequest().permitAll()
                    .and()
                    .csrf().disable();

        }
    }

    @Configuration
    @Order(2)
    public static class H2SecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/h2-console/**")
                    .authorizeRequests()
                    .anyRequest().permitAll()
                    .and()
                    .csrf().disable()
                    .headers().frameOptions().disable();

        }
    }

    @Configuration
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .authorizeRequests()
                    .antMatchers("/", "/login/*", "/logout").hasAnyRole("ANONYMOUS", "USER")
                    .antMatchers("/admin/**", "/events/").hasRole("ADMIN")
                    .antMatchers("/**").hasRole("USER");
            http
                    .exceptionHandling().accessDeniedPage("/errors/403");
            http
                    .formLogin()
                    .loginPage("/login/form")
                    .loginProcessingUrl("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .permitAll()
                    .failureUrl("/login/form?error");
            http
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login/form?logout")
                    .permitAll();
        }

        @Override
        public void configure(WebSecurity web) {
//            web.ignoring().antMatchers("");
        }
    }
}
