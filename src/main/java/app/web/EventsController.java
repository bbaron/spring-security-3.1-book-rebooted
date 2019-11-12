package app.web;

import app.domain.CalendarUser;
import app.domain.Event;
import app.service.CalendarService;
import app.service.UserContext;
import app.web.model.CreateEventForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/events")
public class EventsController {
    private final CalendarService calendarService;
    private final UserContext userContext;

    @Autowired
    public EventsController(CalendarService calendarService, UserContext userContext) {
        this.calendarService = calendarService;
        this.userContext = userContext;
    }

    @GetMapping("/")
    public ModelAndView events() {
        return new ModelAndView("events/list", "events", calendarService.getEvents());
    }

    @GetMapping("/my")
    public ModelAndView myEvents() {
        CalendarUser currentUser = userContext.getCurrentUser();
        Integer currentUserId = currentUser.getId();
        ModelAndView result = new ModelAndView("events/my", "events", calendarService.findForUser(currentUserId));
        result.addObject("currentUser", currentUser);
        return result;
    }

    @GetMapping("/{eventId}")
    public ModelAndView show(@PathVariable int eventId) {
        Event event = calendarService.getEvent(eventId);
        if (event == null) {
            throw new NotFoundException("No such event with id " + eventId);
        }
        return new ModelAndView("events/show", "event", event);
    }

    @GetMapping("/form")
    public String createEventForm(Model model
                                  //@ModelAttribute CreateEventForm createEventForm
            ) {
        var createEventForm = new CreateEventForm();
        createEventForm.setSummary("A new event....");
        createEventForm.setDescription("This was auto-populated to save time creating a valid event.");
        createEventForm.setWhen(LocalDateTime.now());

        // make the attendee not the current user
        CalendarUser currentUser = userContext.getCurrentUser();
        int attendeeId = currentUser.getId() == 0 ? 1 : 0;
        CalendarUser attendee = calendarService.getUser(attendeeId);
        createEventForm.setAttendeeEmail(attendee.getEmail());
        model.addAttribute("createEventForm", createEventForm);
        return "events/create";
    }

    /**
     * Populates the form for creating an event with valid information. Useful so that users do not have to think when
     * filling out the form for testing.
     *
     * @param createEventForm the form
     * @return the template path
     */
    @GetMapping(path = "/new", params = "auto")
    public String createEventFormAutoPopulate(@ModelAttribute CreateEventForm createEventForm) {
        // provide default values to make user submission easier
        createEventForm.setSummary("A new event...");
        createEventForm.setDescription("This was auto populated to save time creating a valid event.");
        createEventForm.setWhen(LocalDateTime.now());

        // make the attendee not the current user
        CalendarUser currentUser = userContext.getCurrentUser();
        int attendeeId = currentUser.getId() == 0 ? 1 : 0;
        CalendarUser attendee = calendarService.getUser(attendeeId);
        createEventForm.setAttendeeEmail(attendee.getEmail());

        return "events/create";
    }

    @PostMapping(path = "/new")
    public String createEvent(@Valid CreateEventForm createEventForm, BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "events/create";
        }
        CalendarUser attendee = calendarService.findUserByEmail(createEventForm.getAttendeeEmail());
        if (attendee == null) {
            result.rejectValue("attendeeEmail", "attendeeEmail.missing",
                    "Could not find a user for the provided Attendee Email");
        }
        if (result.hasErrors()) {
            return "events/create";
        }
        var event = Event.builder();
        event.attendee(attendee);
        event.description(createEventForm.getDescription());
        event.owner(userContext.getCurrentUser());
        event.summary(createEventForm.getSummary());
        event.when(createEventForm.getWhen());
        calendarService.createEvent(event.build());
        redirectAttributes.addFlashAttribute("message", "Successfully added the new event");
        return "redirect:/events/my";
    }
}