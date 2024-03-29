package app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login/form").setViewName("login");
        registry.addViewController("/errors/403").setViewName("errors/403");
        registry.addViewController("/bootstrap4").setViewName("bootstrap4");
    }

}