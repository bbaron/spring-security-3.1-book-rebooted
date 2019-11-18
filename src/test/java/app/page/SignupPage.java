package app.page;

import app.domain.CalendarUser;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

public class SignupPage extends PageObject {
    SignupPage(WebClient webClient, HtmlPage page) {
        super(webClient, page, "Signup");
    }

    public WelcomePage signup(CalendarUser user) throws IOException {
        HtmlForm form = super.page.getFormByName("signup");
        form.reset();
        form.getInputByName("email").type(user.getEmail());
        form.getInputByName("firstName").type(user.getFirstName());
        form.getInputByName("lastName").type(user.getLastName());
        form.getInputByName("password").type(user.getPassword());
        var submitButton = form.getInputByName("submitButton");
        var landing = (HtmlPage) submitButton.click();
        return new WelcomePage(webClient, landing);
    }
}
