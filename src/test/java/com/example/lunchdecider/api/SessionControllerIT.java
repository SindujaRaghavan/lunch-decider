package com.example.lunchdecider.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void fullFlowWorks() throws Exception {
        // create session by predefined user
        var createRes = mvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("createdByUsername", "alice"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", not(isEmptyOrNullString())))
                .andReturn();

        String code = om.readTree(createRes.getResponse().getContentAsString()).get("code").asText();

        // join as bob
        mvc.perform(post("/api/sessions/{code}/join", code)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("username", "bob"))))
                .andExpect(status().isOk());

        // bob submits restaurant
        mvc.perform(post("/api/sessions/{code}/restaurants", code)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("username", "bob", "restaurantName", "Din Tai Fung"))))
                .andExpect(status().isOk());

        // alice ends
        mvc.perform(post("/api/sessions/{code}/end", code).param("by", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ENDED")))
                .andExpect(jsonPath("$.pickedRestaurant", anyOf(is("Din Tai Fung"), nullValue())));
    }

    @Test
    void cannotJoinAfterEnded() throws Exception {
        var createRes = mvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("createdByUsername", "alice"))))
                .andReturn();
        String code = om.readTree(createRes.getResponse().getContentAsString()).get("code").asText();

        mvc.perform(post("/api/sessions/{code}/end", code).param("by", "alice"))
                .andExpect(status().isOk());

        mvc.perform(post("/api/sessions/{code}/join", code)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("username", "bob"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("already ended")));
    }
}
