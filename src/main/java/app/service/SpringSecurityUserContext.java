package app.service;

import app.domain.CalendarUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * An implementation of {@link UserContext} that looks up the {@link CalendarUser} using the Spring Security's
 * {@link Authentication} by principal name.
 *
 * @author Rob Winch
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class SpringSecurityUserContext implements UserContext {
    private final CalendarService calendarService;
    private final UserDetailsService userDetailsService;

    private static Optional<SecurityContext> getSecurityContext() {
        return Optional.ofNullable(SecurityContextHolder.getContext());
    }

    private static Optional<Authentication> getAuthentication() {
        return getSecurityContext().map(SecurityContext::getAuthentication);
    }

    /**
     * Get the {@link CalendarUser} by obtaining the currently logged in Spring Security user's
     * {@link Authentication#getName()} and using that to find the {@link CalendarUser} by email address (since for our
     * application Spring Security usernames are email addresses).
     */
    @Override
    public CalendarUser getCurrentUser() {
        var email = getAuthentication()
                .map(Authentication::getName)
                .orElse(null);
        if (email == null) return null;
        var result = calendarService.findUserByEmail(email);
        if (result == null)
            throw new IllegalStateException("Spring Security is not in sync with CalendarUsers." +
                    " Could not find user with email " + email);
        log.debug("Current user: {}", result);
        return result;
    }

    @Override
    public void setCurrentUser(CalendarUser user) {
        throw new UnsupportedOperationException();
    }
}