package de.unibremen.gradelog.controller;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.faces.component.UIComponent;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.SessionDAO;
import de.unibremen.gradelog.persistence.UserDAO;

/*
 * @author Rune Krauss
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {
	@InjectMocks
	@Spy
	private LoginController loginBean;

	@Mock
	private UserDAO userDao;

	@Mock
	private SessionDAO sessionDao;

	@Mock
	private Session session;

	@Mock
	private User user;

	@Mock
	private User dbUser;

	@Mock
	UIComponent button;

	public LoginControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(loginBean, "userDAO", userDao);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testLoginSuccess() {
		when(session.isLoggedIn()).thenReturn(false);
		when(userDao.getUserByUsername("Bob")).thenReturn(dbUser);
		when(dbUser.getPassword()).thenReturn("Test123#");
		when(session.getUser()).thenReturn(null);
		doNothing().when((AbstractController) loginBean).setLocale();
		Whitebox.setInternalState(loginBean, "username", "Bob");
		Whitebox.setInternalState(loginBean, "password", "Test123#");
		assertEquals("index.xhtml?faces-redirect=true", loginBean.login());
	}

	@Test
	public void testLoginWithAlreadyLoggedInUser() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) loginBean).setLocale();
		assertNull(loginBean.login());
	}

	@Test
	public void testLoginForUnknownUser() {
		when(session.isLoggedIn()).thenReturn(false);
		when(userDao.getUserByUsername("Bob")).thenReturn(null);
		when(session.getUser()).thenReturn(user);

		doNothing().when((AbstractController) loginBean).addMessage(button, "errorUnknownUsername");
		Whitebox.setInternalState(loginBean, "username", "Bob");
		Whitebox.setInternalState(loginBean, "loginButton", button);
		assertNull(loginBean.login());
	}

	@Test
	public void testLoginForWrongPassword() {
		when(session.isLoggedIn()).thenReturn(false);
		when(userDao.getUserByUsername("Bob")).thenReturn(dbUser);
		when(dbUser.getPassword()).thenReturn("Test523#");
		when(session.getUser()).thenReturn(null);

		doNothing().when((AbstractController) loginBean).addMessage(button, "errorUnknownPassword");
		Whitebox.setInternalState(loginBean, "username", "Bob");
		Whitebox.setInternalState(loginBean, "password", "Test123#");
		Whitebox.setInternalState(loginBean, "loginButton", button);
		assertNull(loginBean.login());
	}

	@Test
	public void testLogoutWithoutLogin() {
		when(session.isLoggedIn()).thenReturn(false);
		assertNull(loginBean.logout());
	}

	@Test
	public void testLogout() {
		when(session.isLoggedIn()).thenReturn(true);
		when(session.getUser()).thenReturn(user);
		doNothing().when((AbstractController) loginBean).setInvalidateSession();
		assertEquals("/scheduler/index.xhtml?faces-redirect=true", loginBean.logout());
	}
}
