package de.unibremen.gradelog.controller;

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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.UserDAO;

/*
 * @author Rune Krauss
 */
@RunWith(MockitoJUnitRunner.class)
public class ProfileControllerTest {

	@InjectMocks
	@Spy
	private ProfileController profileBean;

	@Mock
	private UserDAO userDAO;

	@Mock
	private Session session;

	@Mock
	private User user;

	public ProfileControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(profileBean, "userDAO", userDAO);
		Whitebox.setInternalState(profileBean, "user", user);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testShowWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) profileBean).addMessage(any());

		assertNull(profileBean.show());
	}

	@Test
	public void testBackWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) profileBean).addMessage(any());

		assertNull(profileBean.back());
	}
}