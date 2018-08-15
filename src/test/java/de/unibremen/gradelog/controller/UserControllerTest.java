package de.unibremen.gradelog.controller;

import de.unibremen.gradelog.persistence.RepresentationDAO;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.model.Profile;
import de.unibremen.gradelog.persistence.SessionDAO;
import de.unibremen.gradelog.persistence.UserDAO;

/*
 * @author Rune Krauss
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

	@InjectMocks
	@Spy
	private UserController userBean;

	@Mock
	private UserDAO userDAO;

	@Mock
	private RepresentationDAO representationDAO;

	@Mock
	private SessionDAO sessionDAO;

	@Mock
	private Session session;

	@Mock
	private User user;

	@Mock
	private Profile profile;

	@Mock
	private User selectedUser;

	@Mock
	UploadedFile file;

	@Mock
	FileUploadEvent fue;

	public UserControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(userBean, "user", user);
		Whitebox.setInternalState(userBean, "selectedUser", selectedUser);
		Whitebox.setInternalState(userBean, "userDAO", userDAO);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testSaveSuccess() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) userBean).addMessage(any());
		when(user.getProfile()).thenReturn(profile);
		when(profile.getAboutMe()).thenReturn("Test");

		userBean.save();
		verify(userDAO).update(user);
	}

	@Test
	public void testSaveWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) userBean).addMessage(any());

		userBean.save();
		verify(userDAO, never()).update(user);
	}

	@Test
	public void testUploadSuccess() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) userBean).addMessage(any());
		when(user.getFile()).thenReturn(file);
		when(fue.getFile()).thenReturn(file);
		when(user.getProfile()).thenReturn(profile);
		byte[] content = {};
		when(file.getContents()).thenReturn(content);

		userBean.upload(fue);
		verify(userDAO).update(user);
	}

	@Test
	public void testUploadWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) userBean).addMessage(any());

		userBean.upload(fue);
		verify(userDAO, never()).update(user);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUploadFailParameterNull() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) userBean).addMessage(any());
		when(user.getFile()).thenReturn(file);
		when(fue.getFile()).thenReturn(null);

		userBean.upload(fue);
	}

}
