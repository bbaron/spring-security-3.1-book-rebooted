package app.page;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.assertj.core.api.Assertions;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class PageObject {
    final WebClient webClient;
    final HtmlPage page;

    PageObject(WebClient webClient, HtmlPage page, String title) {
        this.webClient = webClient;
        this.page = page;
        assertThat(page.getTitleText()).isEqualTo(title);
    }

    PageObject(WebClient webClient, String url, String title) throws IOException {
        this.webClient = webClient;
        this.page = this.webClient.getPage(url);
        assertThat(this.page.getTitleText()).isEqualTo(title);
    }

}
