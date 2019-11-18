package app.authentication;

import app.core.userdetails.CalendarUserDetailsService;
import app.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static app.core.authority.CalendarUserAuthorityUtils.createAuthorities;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

/**
 * A Spring Security {@link AuthenticationProvider} that uses our {@link CalendarService} for authentication. Compare
 * this to our {@link CalendarUserDetailsService} which is called by Spring Security's {@link DaoAuthenticationProvider}.
 *
 * @author Rob Winch
 * @see CalendarUserDetailsService
 */
@Component
@RequiredArgsConstructor
public class CalendarUserAuthenticationProvider implements AuthenticationProvider {
    private final CalendarService calendarService;
    private final PasswordEncoder encoder = createDelegatingPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var token = (DomainUsernamePasswordAuthenticationToken) authentication;
        var userName = token.getName();
        var domain = token.getDomain();
        if (userName == null || domain == null) {
            throw new UsernameNotFoundException("Invalid username/password");
        }
        var email = userName + "@" + domain;
        var user = calendarService.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username/password");
        }
        var password = user.getPassword();
        if (!encoder.matches(token.getCredentials().toString(), password)) {
            throw new BadCredentialsException("Invalid username/password");
        }
        var authorities = createAuthorities(user);
        return new DomainUsernamePasswordAuthenticationToken(user, password, domain, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return DomainUsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}