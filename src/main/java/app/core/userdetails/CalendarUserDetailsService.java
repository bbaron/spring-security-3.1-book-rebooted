package app.core.userdetails;

import app.dataaccess.CalendarUserDao;
import app.domain.CalendarUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static app.core.authority.CalendarUserAuthorityUtils.createAuthorities;

@Service
@RequiredArgsConstructor
public class CalendarUserDetailsService implements UserDetailsService {
    private final CalendarUserDao calendarUserDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = calendarUserDao.findUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new CalendarUserDetails(user);
    }

    public static final class CalendarUserDetails extends CalendarUser implements UserDetails {

        public CalendarUserDetails(CalendarUser u) {
            super(u.getId(), u.getFirstName(), u.getLastName(),
                    u.getEmail(), u.getPassword());
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return createAuthorities(this);
        }

        @Override
        public String getUsername() {
            return getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
