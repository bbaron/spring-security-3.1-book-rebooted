package app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfigs {


    @Configuration
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .authorizeRequests()
                    .antMatchers("/", "/login/*", "/logout", "/signup/*").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
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
            web.ignoring().antMatchers("/css/**", "/h2-console/**", "/img/**", "/js/**", "/bootstrap4");
        }

        @Bean
        public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver, SpringSecurityDialect sec) {
            final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);
            templateEngine.addDialect(sec); // Enable use of "sec"
            return templateEngine;
        }
    }


    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.printf("user1: %s%n", encoder.encode("user1"));
        System.out.printf("user2: %s%n", encoder.encode("user2"));
        System.out.printf("admin1: %s%n", encoder.encode("admin1"));
    }
}