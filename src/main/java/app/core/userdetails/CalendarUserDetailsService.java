package app.core.userdetails;

import app.dataaccess.CalendarUserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static app.core.authority.CalendarUserAuthorityUtils.createAuthorities;

@Service
@RequiredArgsConstructor
public class CalendarUserDetailsService implements UserDetailsService {
    private final CalendarUserDao calendarUserDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = calendarUserDao.findUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
        var authorities = createAuthorities(user);

        return User.withDefaultPasswordEncoder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
