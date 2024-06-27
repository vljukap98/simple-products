package com.ljakovic.simpleproducts.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testProductListEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/product/all").header("api-key", apiKey))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
