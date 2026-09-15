package com.devopslab;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    @Test
    void testMessage() {
        assertEquals(
            "DevOps CI/CD Pipeline - Build, Test and Deploy!",
            App.getMessage()
        );
    }
}
