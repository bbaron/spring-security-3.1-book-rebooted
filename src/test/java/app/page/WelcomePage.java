package app.page;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

import static app.HtmlUnitHelpers.getAnchorById;

public class WelcomePage extends PageObject {

    public WelcomePage(WebClient webClient) throws IOException {
        super(webClient, "/", "Getting Started: Serving Web Content");
    }

    public WelcomePage(WebClient webClient, HtmlPage page) {
        super(webClient, page, "Getting Started: Serving Web Content");
    }

    public SignupPage signupPage() throws IOException {
        HtmlAnchor link = getAnchorById(page, "signupUrl");
        HtmlPage signUpPage = link.click();
        return new SignupPage(webClient, signUpPage);
    }
}
