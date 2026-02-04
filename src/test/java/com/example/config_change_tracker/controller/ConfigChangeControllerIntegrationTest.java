package com.example.config_change_tracker.controller;

import com.example.config_change_tracker.domain.ChangeType;
import com.example.config_change_tracker.domain.RuleType;
import com.example.config_change_tracker.dto.CreateConfigChangeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class ConfigChangeControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // ---------- CREATE ----------

    @Test
    void create_CREATE_Returns201() throws Exception {
        CreateConfigChangeRequest req = baseCreate();
        req.setChangeType(ChangeType.CREATE);
        req.setAfter("{\"limit\":1000}");

        mockMvc.perform(post("/config-change").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.before").doesNotExist())
                .andExpect(jsonPath("$.after").exists());
    }

    @Test
    void create_UPDATE_Returns201() throws Exception {
        CreateConfigChangeRequest req = baseCreate();
        req.setChangeType(ChangeType.UPDATE);

        req.setBefore("{\"limit\":500}");
        req.setAfter("{\"limit\":1000}");

        mockMvc.perform(post("/config-change").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.changeType").value("UPDATE"));
    }

    @Test
    void create_DELETE_Returns201() throws Exception {
        CreateConfigChangeRequest req = baseCreate();
        req.setChangeType(ChangeType.DELETE);
        req.setBefore("{\"limit\":1000}");

        mockMvc.perform(post("/config-change").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.after").doesNotExist());
    }

    @Test
    void create_InvalidRequest_Returns400() throws Exception {
        CreateConfigChangeRequest req = baseCreate();
        req.setChangeType(ChangeType.UPDATE);
        req.setAfter("{\"limit\":1000}"); // missing before

        mockMvc.perform(post("/config-change").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createChange_invalidChangeType_returnsBadRequest() throws Exception {
        String invalidJson = """
            {
                "ruleType": "CREDIT_LIMIT",
                "changeType": "INVALID_TYPE",
                "changedBy": "user",
                "critical": true,
                "before": null,
                "after": "limit=2000",
                "changedBy": "system-admin"
            }
            """;

        mockMvc.perform(post("/config-change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // ---------- GET BY ID ----------

    @Test
    void getById_Existing_Returns200() throws Exception {
        String id = createAndReturnId();

        mockMvc.perform(get("/config-change/{id}", id))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getById_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/config-change/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
    }

    // ---------- LIST ----------

    @Test
    void list_ReturnsArray() throws Exception {
        createAndReturnId();

        mockMvc.perform(get("/config-change"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void list_FilterByRuleType() throws Exception {
        createAndReturnId();

        mockMvc.perform(get("/config-change").param("ruleType", "CREDIT_LIMIT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].ruleType", everyItem(is("CREDIT_LIMIT"))));
    }

    // ---------- E2E ----------

    @Test
    void e2e_Create_Get_List() throws Exception {
        String id = createAndReturnId();

        mockMvc.perform(get("/config-change/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/config-change"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '%s')]", id).exists());
    }

    // ---------- helpers ----------

    private CreateConfigChangeRequest baseCreate() {
        CreateConfigChangeRequest req = new CreateConfigChangeRequest();
        req.setRuleType(RuleType.CREDIT_LIMIT);
        req.setChangedBy("test@example.com");
        req.setCritical(false);
        return req;
    }

    private String createAndReturnId() throws Exception {
        CreateConfigChangeRequest req = baseCreate();
        req.setChangeType(ChangeType.CREATE);
        req.setAfter("{\"limit\":1000}");

        String json = mockMvc.perform(post("/config-change")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(json).get("id").asText();
    }
}

