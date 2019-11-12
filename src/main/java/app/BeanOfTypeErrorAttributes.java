package app;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

public class BeanOfTypeErrorAttributes implements ErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        return null;
    }

    @Override
    public Throwable getError(WebRequest webRequest) {
        return null;
    }
}
