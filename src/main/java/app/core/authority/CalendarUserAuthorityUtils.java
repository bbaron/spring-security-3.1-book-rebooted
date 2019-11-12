package app.core.authority;

import app.domain.CalendarUser;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

/**
 * A utility class used for creating the {@link GrantedAuthority}'s given a {@link CalendarUser}. In a real solution
 * this would be looked up in the existing system, but for simplicity our original system had no notion of authorities.
 *
 * @author Rob Winch
 */
@UtilityClass
public class CalendarUserAuthorityUtils {
    private static final List<GrantedAuthority> ADMIN_ROLES = createAuthorityList("ROLE_ADMIN", "ROLE_USER");
    private static final List<GrantedAuthority> USER_ROLES = createAuthorityList("ROLE_USER");

    public static Collection<? extends GrantedAuthority> createAuthorities(CalendarUser calendarUser) {
        var username = calendarUser.getEmail();
        if (username.startsWith("admin")) {
            return ADMIN_ROLES;
        }
        return USER_ROLES;
    }

}