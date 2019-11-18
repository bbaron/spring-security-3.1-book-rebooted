package app;

import app.domain.CalendarUser;
import app.page.WelcomePage;
import app.service.CalendarService;
import app.service.UserContext;
import app.web.SignupController;
import app.web.WelcomeController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest({WelcomeController.class, SignupController.class})
@ActiveProfiles("test")
class WelcomeControllerHtmlUnitTests {
    @Autowired
    private WebClient webClient;

    @MockBean
    UserDetailsService userDetailsService;
    @MockBean
    UserContext userContext;
    @MockBean
    CalendarService calendarService;

    @Test
    void testWelcomePage() throws Exception {
        CalendarUser user = CalendarUser.builder()
                .email("test@example.com")
                .firstName("f")
                .lastName("l")
                .password("pass")
                .build();
        when(calendarService.createUser(user)).thenReturn(user.withId(1000));
        HtmlPage page = webClient.getPage("/");
        assertThat(page.getTitleText()).isEqualTo("Getting Started: Serving Web Content");
        assertThat(page.getElementById("loginUrl")).isNotNull();
        assertThat(page.getElementById("signupUrl")).isNotNull();
        HtmlAnchor link = HtmlUnitHelpers.getAnchorById(page, "signupUrl");
        HtmlPage signUpPage = link.click();
        HtmlForm form = signUpPage.getFormByName("signup");
        form.reset();
        form.getInputByName("email").type(user.getEmail());
        form.getInputByName("firstName").type(user.getFirstName());
        form.getInputByName("lastName").type(user.getLastName());
        form.getInputByName("password").type(user.getPassword());
        var submitButton = form.getInputByName("submitButton");
        var landing = (HtmlPage) submitButton.click();
        assertThat(landing.getTitleText()).isEqualTo("Getting Started: Serving Web Content");
        var message = landing.getElementById("message");
        assertThat(message) .isNotNull();
        assertThat(message.getTextContent()).isEqualTo("You have successfully signed up and logged in.");
    }

    @Test
    void testSignup() throws Exception {
        CalendarUser user = CalendarUser.builder()
                .email("test@example.com")
                .firstName("f")
                .lastName("l")
                .password("pass")
                .build();
        when(calendarService.createUser(user)).thenReturn(user.withId(1000));
        var welcomePage = new WelcomePage(webClient);
        var signupPage = welcomePage.signupPage();
        var landing = signupPage.signup(user);
        assertThat(landing).isNotNull();
    }
}
