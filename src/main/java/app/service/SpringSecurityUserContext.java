package app.service;

import app.domain.CalendarUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

import static app.core.authority.CalendarUserAuthorityUtils.createAuthorities;

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

    private static Optional<SecurityContext> getSecurityContext() {
        return Optional.ofNullable(SecurityContextHolder.getContext());
    }

    private static SecurityContext getRequiredSecurityContext() {
        return getSecurityContext().orElseThrow();
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
        var auth = getAuthentication()
                .orElse(null);
        if (auth == null) return null;
        var result = (CalendarUser) auth.getPrincipal();
        log.debug("Current user: {}", result);
        return result;
    }

    @Override
    public void setCurrentUser(CalendarUser user) {
        Assert.notNull(user, "user cannot be null");
        var token = new UsernamePasswordAuthenticationToken(user, user.getPassword(), createAuthorities(user));
        getRequiredSecurityContext().setAuthentication(token);
    }
}