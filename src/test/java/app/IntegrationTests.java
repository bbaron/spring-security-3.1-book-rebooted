package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class IntegrationTests {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/login/form", "/css/bootstrap.css"})
    void givenAnonymousUser_requestIsOk(String path) throws Exception {
        mvc.perform(get(path)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/events/", "/events/my/", "/events/100", "/events/form"})
    void givenAnonymousUser_requestIsRedirectedToLogin(String path) throws Exception {
        mvc.perform(get(path))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", containsStringIgnoringCase("/login/form")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/events/"})
    @WithMockUser1
    void givenUser_requestIsForbidden(String path) throws Exception {
        mvc.perform(get(path))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/events/"})
    @WithMockAdminUser
    void givenAdminUser_requestIsOk(String path) throws Exception {
        mvc.perform(get(path))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/events/my/", "/events/100", "/events/form"})
    @WithMockUser1
    void givenUser_requestIsOk(String path) throws Exception {
        mvc.perform(get(path))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/logout"})
    @WithMockUser1
    void givenUser_postRequestIsSuccess(String path) throws Exception {
        mvc.perform(post(path).with(csrf()))
                .andExpect(status().isFound());
    }

}
