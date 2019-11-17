package app.web;

import app.domain.CalendarUser;
import app.service.CalendarService;
import app.service.UserContext;
import app.web.model.SignupForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SignupController {
    private final UserContext userContext;
    private final CalendarService calendarService;
    private final UserDetailsService userDetailsService;

    @GetMapping("/signup/form")
    public String signup(Model model) {
        var form = new SignupForm();
        form.setFirstName("Fred");
        form.setLastName("Flintstone");
        form.setPassword("user");
        form.setEmail("flintstone@example.com");
        model.addAttribute("signupForm", form);
        return "signup/form";
    }

    @PostMapping(value = "/signup/new")
    public String signup(@Valid SignupForm signupForm, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "signup/form";
        }

        String email = signupForm.getEmail();
        if (calendarService.findUserByEmail(email) != null) {
            result.rejectValue("email", "errors.signup.email", "Email address is already in use.");
            return "signup/form";
        }

        final var user = CalendarUser.builder()
                .email(email)
                .firstName(signupForm.getFirstName())
                .lastName(signupForm.getLastName())
                .password(signupForm.getPassword())
                .build();

        final var newUser = calendarService.createUser(user);
        var userDetails = User.withDefaultPasswordEncoder()
                .username(newUser.getEmail())
                .password(newUser.getPassword())
                .roles("USER")
                .build();
        if (userDetailsService instanceof InMemoryUserDetailsManager) {
            var imudm = (InMemoryUserDetailsManager) userDetailsService;
            imudm.createUser(userDetails);
        }
        userContext.setCurrentUser(newUser);

        redirectAttributes.addFlashAttribute("message", "You have successfully signed up and logged in.");
        return "redirect:/";
    }
}