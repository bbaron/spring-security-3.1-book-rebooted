package app.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * This displays the welcome screen that shows what will be happening in this chapter.
 *
 * @author Rob Winch
 */
@Controller
@RequiredArgsConstructor
public class WelcomeController {
    private final WebInvocationPrivilegeEvaluator webInvocationPrivilegeEvaluator;

    @GetMapping("/")
    public String welcome() {
        return "index";
    }

    @ModelAttribute("showCreateLink")
    public boolean showCreateLink(Authentication authentication) {
        return authentication != null && authentication.getName().contains("user");
    }
    @ModelAttribute("showAdminLink")
    public boolean showAdminLink(Authentication authentication) {
        return webInvocationPrivilegeEvaluator.isAllowed("/admin", authentication);
    }
}