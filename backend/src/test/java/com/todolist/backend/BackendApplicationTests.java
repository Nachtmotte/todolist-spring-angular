package com.todolist.backend;

import com.todolist.backend.controller.AdminControllerIntegrationTest;
import com.todolist.backend.controller.TokenControllerIntegrationTest;
import com.todolist.backend.controller.UserControllerIntegrationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		UserControllerIntegrationTest.class,
		AdminControllerIntegrationTest.class,
		TokenControllerIntegrationTest.class
})
public class BackendApplicationTests {

}
