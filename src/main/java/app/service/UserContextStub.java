package app.service;

import app.dataaccess.CalendarUserDao;
import app.domain.CalendarUser;
import lombok.RequiredArgsConstructor;

/**
 * Returns the same user for every call to {@link #getCurrentUser()}. This is used prior to adding security, so that the
 * rest of the application can be used.
 *
 * @author Rob Winch
 */
@RequiredArgsConstructor
public class UserContextStub implements UserContext {
    private final CalendarUserDao userService;
    /**
     * The {@link CalendarUser#getId()} for the user that is representing the currently logged in user. This can be
     * modified using {@link #setCurrentUser(CalendarUser)}
     */
    private int currentUserId = 0;

    @Override
    public CalendarUser getCurrentUser() {
        return userService.getUser(currentUserId);
    }

    @Override
    public void setCurrentUser(CalendarUser user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        var currentId = user.getId();
        if (currentId == null) {
            throw new IllegalArgumentException("user.getId() cannot be null");
        }
        this.currentUserId = currentId;
    }
}