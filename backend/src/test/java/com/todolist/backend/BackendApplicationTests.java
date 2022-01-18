package com.todolist.backend;

import com.todolist.backend.controller.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		UserControllerIntegrationTest.class,
		AdminControllerIntegrationTest.class,
		TokenControllerIntegrationTest.class,
		ProfilePictureControllerIntegrationTest.class,
		TodoListControllerIntegrationTest.class,
		ItemControllerIntegrationTest.class
})
public class BackendApplicationTests {

}
