package app;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({METHOD, TYPE})
@Retention(RUNTIME)
@Inherited
@Documented
@WithMockUser(roles = {"USER", "ADMIN"}, username = "admin1@example.com")
@interface WithMockAdminUser {
}
