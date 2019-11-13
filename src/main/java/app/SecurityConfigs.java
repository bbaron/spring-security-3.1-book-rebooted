package app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

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
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .authorizeRequests()
                    .antMatchers("/", "/login/*", "/logout", "/signup/*").permitAll()
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
                    .defaultSuccessUrl("/default")
                    .failureUrl("/login/form?error");
            http
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login/form?logout")
                    .permitAll();
        }

        @Override
        public void configure(WebSecurity web) {
            web.ignoring().antMatchers("/css/**", "/h2-console/**");
        }

        @Bean
        public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver, SpringSecurityDialect sec) {
            final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);
            templateEngine.addDialect(sec); // Enable use of "sec"
            return templateEngine;
        }
    }
}
