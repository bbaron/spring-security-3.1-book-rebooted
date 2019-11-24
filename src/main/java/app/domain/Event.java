package app.domain;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * An {@link Event} is an item on a calendar that contains an owner (the person who created it), an attendee
 * (someone who was invited to the event), when the event will occur, a summary, and a description. For simplicity, all
 * fields are required.
 *
 * @author Rob Winch
 */
@Getter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class Event {
    @With
    private final Integer id;

    @NotBlank
    private final String summary;
    @NotBlank
    private final String description;
    @NotNull
    private final LocalDateTime when;
    @NotNull
    private final CalendarUser owner;
    private final CalendarUser attendee;

}
