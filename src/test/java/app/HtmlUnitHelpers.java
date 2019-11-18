package app;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.experimental.UtilityClass;

@UtilityClass
class HtmlUnitHelpers {
    static HtmlAnchor getAnchorById(HtmlPage page, String id) {
        return (HtmlAnchor) page.getElementById(id);
    }
}
