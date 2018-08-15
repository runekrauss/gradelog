package de.unibremen.gradelog.controller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Ignore;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import de.unibremen.gradelog.model.Page;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.PageDAO;
import de.unibremen.gradelog.persistence.RepresentationDAO;
import de.unibremen.gradelog.persistence.SessionDAO;
import de.unibremen.gradelog.persistence.UserDAO;

/*
 * @author Rune Krauss
 */
@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {

	@InjectMocks
	@Spy
	private AdminController adminBean;

	@Mock
	private UserDAO userDAO;

	@Mock
	private SessionDAO sessionDAO;

	@Mock
	private PageDAO pageDAO;
	
	@Mock
	private RepresentationDAO representationDAO;

	@Mock
	private Session session;

	@Mock
	private User user;
	
	@Mock
	private User dbUser;

	@Mock
	private User selectedUser;
	
	@Mock
	private List<User> selectedUsers;

	@Mock
	private Page selectedPage;

	@Mock
	private RowEditEvent event;

	@Mock
	private FileUploadEvent fue;

	@Mock
	UploadedFile file;

	public AdminControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(adminBean, "user", user);
		Whitebox.setInternalState(adminBean, "selectedPage", selectedPage);
		Whitebox.setInternalState(adminBean, "userDAO", userDAO);
		Whitebox.setInternalState(adminBean, "pageDAO", pageDAO);
		Whitebox.setInternalState(adminBean, "representationDAO", representationDAO);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testOnRowEditSuccess() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) adminBean).addMessage(any());
		when(event.getObject()).thenReturn(user);
		when(user.getId()).thenReturn(3);
		when(session.getUser()).thenReturn(dbUser);
		when(dbUser.getId()).thenReturn(2);

		adminBean.onRowEdit(event);
		verify(userDAO).update(user);
	}
	
	@Test
	public void testOnRowEditFailedOwnUserRole() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) adminBean).addMessage(any());
		when(event.getObject()).thenReturn(user);
		when(user.getId()).thenReturn(3);
		when(session.getUser()).thenReturn(dbUser);
		when(dbUser.getId()).thenReturn(3);
		when(user.getUserRole()).thenReturn("admin");
		when(dbUser.getUserRole()).thenReturn("teacher");

		adminBean.onRowEdit(event);
		verify(userDAO, never()).update(user);
	}
	
	@Test
	public void testOnRowEditFailedBlockedYourself() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) adminBean).addMessage(any());
		when(event.getObject()).thenReturn(user);
		when(user.getId()).thenReturn(3);
		when(session.getUser()).thenReturn(dbUser);
		when(dbUser.getId()).thenReturn(3);
		when(user.getUserRole()).thenReturn("admin");
		when(dbUser.getUserRole()).thenReturn("admin");
		when(user.isBlocked()).thenReturn(true);

		adminBean.onRowEdit(event);
		verify(userDAO, never()).update(user);
	}

	@Test
	public void testOnRowEditWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);

		adminBean.onRowEdit(event);
		verify(userDAO, never()).update(user);
	}

	@Test
	@Ignore
	public void testRemoveSuccess() throws Exception {
		when(sessionDAO.isUserLoggedIn(user)).thenReturn(false);
		doNothing().when((AbstractController) adminBean).addMessage(any());
		when(event.getObject()).thenReturn(user);
		when(selectedUser.getLogin()).thenReturn("bob");
		when(session.getUser()).thenReturn(user);
		when(user.getLogin()).thenReturn("alice");
		doReturn(session).when((AbstractController) adminBean).getSession();

		adminBean.setSelectedUsers(selectedUsers);
		adminBean.remove();
		verify(userDAO).delete(selectedUser);
	}

	@Test
	public void testRemoveWithoutPermission() throws Exception {
		when(sessionDAO.isUserLoggedIn(user)).thenReturn(true);
		doNothing().when((AbstractController) adminBean).addMessage(any());

		adminBean.remove();
		verify(userDAO, never()).delete(selectedUser);
	}

	@Test
	public void testCreateSuccess() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) adminBean).addMessage(any());

		adminBean.create();
		verify(userDAO).create(user);
	}

	@Test
	public void testCreateWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) adminBean).addMessage(any());

		adminBean.create();
		verify(userDAO, never()).create(user);
	}

	@Test
	public void testSavePageSuccess() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) adminBean).addMessage(any());
		when(selectedPage.getGermanContent()).thenReturn("Test");
		when(selectedPage.getEnglishContent()).thenReturn("Test");

		adminBean.savePage();
		verify(pageDAO).update(selectedPage);
	}

	@Test
	public void testSavePageWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) adminBean).addMessage(any());

		adminBean.savePage();
		verify(pageDAO, never()).update(selectedPage);
	}

	@Test
	public void testUploadDIFFWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) adminBean).addMessage(any());

		adminBean.uploadDIF(fue);
		verify(userDAO, never()).create(user);
	}
}
