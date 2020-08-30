package org.example.ticketcampaign.endpoints

import groovy.json.JsonSlurper
import org.example.ticketcampaign.BaseIntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

import static groovy.json.JsonOutput.toJson
import static java.nio.charset.StandardCharsets.UTF_8
import static org.springframework.http.MediaType.APPLICATION_JSON

@AutoConfigureMockMvc
class BaseControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc

    protected fromJson(String json) {
        new JsonSlurper().parse(json.getBytes(UTF_8))
    }

    protected MockHttpServletResponse performWithJsonContent(MockHttpServletRequestBuilder request, LinkedHashMap<String, Serializable> content) {
        mockMvc.perform(request
                .contentType(APPLICATION_JSON)
                .content(toJson(content)))
                .andReturn().response
    }

    protected MockHttpServletResponse perform(MockHttpServletRequestBuilder request) {
        mockMvc.perform(request).andReturn().response
    }

}
