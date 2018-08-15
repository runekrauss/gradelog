package de.unibremen.gradelog.controller;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.persistence.PageDAO;
import de.unibremen.gradelog.persistence.UserDAO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import de.unibremen.gradelog.model.User;

/*
 * @author Rune Krauss
 */
@RunWith(MockitoJUnitRunner.class)
public class PageControllerTest {

	@InjectMocks
	@Spy
	private PageController pageBean;

	@Mock
	private Session session;

	@Mock
	private PageDAO pageDAO;

	@Mock
	private UserDAO userDAO;

	private List<User> allUsers;

	public PageControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		User user1 = new User();
		user1.setLogin("alice");
		user1.setPassword("apfel");
		user1.setEmail("alice@test.de");
		user1.setOnline(true);
		User user2 = new User();
		user2.setLogin("bob");
		user2.setPassword("birne");
		user2.setEmail("bob@test.de");
		User user3 = new User();
		user2.setLogin("mallory");
		user2.setPassword("kiwi");
		user2.setEmail("mallory@test.de");
		allUsers = new ArrayList<>();
		allUsers.add(user1);
		allUsers.add(user2);
		allUsers.add(user3);
		Whitebox.setInternalState(pageBean, "allUsers", allUsers);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testOnlineUsersAmount() throws Exception {
		assertEquals(1, pageBean.getAllOnlineUsers());
	}

}
