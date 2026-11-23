package com.devopslab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @BeforeEach
    public void setUp() {
        App.getStudentDatabase().clear();
    }

    @Test
    public void testStudentModelCreationAndJson() {
        App.Student student = new App.Student(101, "Test Student", "test@university.edu", "Cloud Computing", 9.5);
        assertEquals(101, student.getId());
        assertEquals("Test Student", student.getName());
        assertEquals("test@university.edu", student.getEmail());
        assertEquals("Cloud Computing", student.getCourse());
        assertEquals(9.5, student.getGpa(), 0.001);

        String json = student.toJson();
        assertTrue(json.contains("\"id\":101"));
        assertTrue(json.contains("\"name\":\"Test Student\""));
        assertTrue(json.contains("\"gpa\":9.50"));
    }

    @Test
    public void testStudentDatabaseOperations() {
        App.Student student = new App.Student(201, "DevOps Engineer", "devops@test.com", "DevOps Lab", 9.8);
        App.getStudentDatabase().put(201, student);

        assertEquals(1, App.getStudentDatabase().size());
        assertTrue(App.getStudentDatabase().containsKey(201));

        App.Student retrieved = App.getStudentDatabase().get(201);
        assertNotNull(retrieved);
        assertEquals("DevOps Engineer", retrieved.getName());

        retrieved.setCourse("Site Reliability Engineering");
        assertEquals("Site Reliability Engineering", App.getStudentDatabase().get(201).getCourse());

        App.getStudentDatabase().remove(201);
        assertEquals(0, App.getStudentDatabase().size());
    }

    @Test
    public void testDefaultPortConfiguration() {
        assertEquals(8080, App.DEFAULT_PORT);
    }
}
