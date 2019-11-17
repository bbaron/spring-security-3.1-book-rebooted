package app.domain;

import lombok.*;

import java.io.Serializable;

import static lombok.AccessLevel.PROTECTED;

/**
 * {@link CalendarUser} is this applications notion of a user.
 * It is good to use your own objects to interact with a
 * user especially in large applications. This ensures that as you evolve your security
 * requirements (update Spring
 * Security, leverage new Spring Security modules, or even swap out security
 * implementations) you can do so easily.
 *
 * @author Rob Winch
 */
@Getter
@EqualsAndHashCode(of = "id")
@RequiredArgsConstructor(access = PROTECTED)
@ToString
@Builder
public class CalendarUser implements Serializable {
    @With
    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;

    public String getName() {
        return String.format("%s, %s", lastName, firstName);
    }

}
