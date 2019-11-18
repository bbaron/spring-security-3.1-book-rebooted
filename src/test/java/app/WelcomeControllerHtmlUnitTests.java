package app;

import app.service.CalendarService;
import app.service.UserContext;
import app.web.SignupController;
import app.web.WelcomeController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

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
        HtmlPage page = webClient.getPage("/");
        assertThat(page.getTitleText()).isEqualTo("Getting Started: Serving Web Content");
        assertThat(page.getElementById("loginUrl")).isNotNull();
        assertThat(page.getElementById("signupUrl")).isNotNull();
        HtmlAnchor link = HtmlUnitHelpers.getAnchorById(page, "signupUrl");
        link.click();
    }
}
