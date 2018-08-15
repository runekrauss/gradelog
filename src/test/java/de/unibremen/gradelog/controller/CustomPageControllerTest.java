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

import java.util.ArrayList;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.Shareable;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.CustomPage;
import de.unibremen.gradelog.model.CustomPageHistory;
import de.unibremen.gradelog.model.Group;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.persistence.CustomPageDAO;
import de.unibremen.gradelog.persistence.GroupDAO;

/*
 * @author Christopher Wojtkow
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomPageControllerTest {

	@InjectMocks
	@Spy
	private CustomPageController pageBean;

	@Mock
	private CustomPageDAO customPageDao;

	@Mock
	private GroupDAO groupDao;

	@Mock
	private UserDAO userDao;
	/*
	 * @Mock private SessionDAO sessionDao;
	 */
	@Mock
	private Session session;

	@Mock
	private User user;

	@Mock
	private User anotherUser;

	@Mock
	private Group group;

	@Mock
	private CustomPage customPage;

	@Mock
	private CustomPage anotherPage;

	@Mock
	private CustomPageHistory customPageHistory;

	/*
	 * @Mock private User dbUser;
	 * 
	 * @Mock UIComponent button = null;
	 */
	public CustomPageControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(pageBean, "customPageDao", customPageDao);
		Whitebox.setInternalState(pageBean, "userDao", userDao);
		Whitebox.setInternalState(pageBean, "groupDao", groupDao);
		Whitebox.setInternalState(pageBean, "session", session);
		Whitebox.setInternalState(pageBean, "user", user);

	}

	@After
	public void tearDown() {
	}

	// null as id
	@Test
	public void testRedirectNullId() {
		assertEquals("", pageBean.redirect(null, "show"));
	}

	// null
	@Test
	public void testRedirectNullNext() {
		assertEquals("", pageBean.redirect("99", null));
	}

	@Test
	public void testRedirectSuccess() {
		assertEquals("show?faces-redirect=true&id=99",
				pageBean.redirect("99", "show"));
	}

	// Normaler Fall, valide id
	@Test
	public void testOnRedirectSuccess() {
		// pageBean.setData("99");

		when(session.isLoggedIn()).thenReturn(true);
		when(customPageDao.getById(99)).thenReturn(customPage);
		when(customPage.getUser()).thenReturn(user);

		pageBean.onRedirect("99", "");

		assertEquals(customPage, pageBean.getCustomPage());
	}

	// Normaler Fall, "" als id
	@Test
	public void testOnRedirectSuccessEmptyString() {
		CustomPage testPage = new CustomPage();
		testPage.setId(99);
		testPage.setUser(user);
		pageBean.setCustomPage(testPage);
		// pageBean.setData("");
		when(session.isLoggedIn()).thenReturn(true);

		pageBean.onRedirect("", "show");

		assertEquals(new CustomPage(),
				pageBean.getCustomPage());
	}

	// Randfall, null als id
	@Test
	public void testOnRedirectNullString() {
		CustomPage testPage = new CustomPage();
		pageBean.setCustomPage(testPage);// init emulieren
		// pageBean.setData(null);

		pageBean.onRedirect(null, "show");

		assertEquals(new CustomPage(),
				pageBean.getCustomPage());
	}

	// Randfall, typ is null
	@Test
	public void testOnRedirectNullType() {
		// pageBean.setData("25");
		when(session.isLoggedIn()).thenReturn(true);
		when(customPageDao.getById(25)).thenReturn(customPage);
		when(customPage.getUser()).thenReturn(user);

		pageBean.onRedirect("25", null);

		assertEquals(new CustomPage(),
				pageBean.getCustomPage());
	}

	// Randfall, gesuchte id ist nicht zugreifbar
	@Test
	public void testOnRedirectInaccessiblePage() {
		CustomPage testPage = new CustomPage();
		testPage.setId(25);
		User user2 = new User();
		user2.setId(55);
		testPage.setUser(user2);
		// pageBean.setData("25");
		when(session.isLoggedIn()).thenReturn(true);
		when(customPageDao.getById(25)).thenReturn(testPage);

		pageBean.onRedirect("25", "");

		assertEquals(new CustomPage(), pageBean.getCustomPage());
	}

	// Randfall, gesuchte id ist nicht vorhanden
	@Test
	public void testOnRedirectPageNotFound() {
		when(session.isLoggedIn()).thenReturn(true);
		when(customPageDao.getById(25)).thenReturn(null);
		// pageBean.setData("25");
		pageBean.onRedirect("25", "show");

		assertEquals(new CustomPage(),
				pageBean.getCustomPage());
	}

	// Randfall, user nicht eingeloggt
	@Test
	public void testOnRedirectNotLoggedIn() {
		CustomPage testPage = new CustomPage();
		pageBean.setCustomPage(testPage);// init emulieren

		when(session.isLoggedIn()).thenReturn(false);
		// pageBean.setData("99");
		pageBean.onRedirect("99", "show");

		assertEquals(new CustomPage(), pageBean.getCustomPage());
	}

	// nicht eingeloggt
	@Test
	public void testSaveNotLoggedIn() {
		when(session.isLoggedIn()).thenReturn(false);

		pageBean.save(new CustomPage());

		verify(customPageDao, never()).create(any());// niemals in saveEdit
		verify(customPageDao, never()).update(any());// niemals in saveNew
	}

	// data ungesetzt -> neue CustomPage; keine SelectedReceivers
	@Test
	public void testSaveNewSuccess() throws DuplicateUsernameException, DuplicateEmailException {
		CustomPage testPage = new CustomPage();
		pageBean.setUser(user);
		pageBean.setCustomPage(testPage);// init emulieren
		pageBean.setSelectedReceivers(new ArrayList<String>());

		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when(customPageDao).create(any());
		doNothing().when((AbstractController) pageBean).addMessage(any());
		doNothing().when(userDao).update(user);

		pageBean.save(testPage);

		verify(userDao, never()).getUserByEmail(any());
		verify(customPageDao).create(any());
		verify((AbstractController) pageBean).addMessage(any());
		verify(customPageDao, never()).update(any());// niemals in saveNew
	}

	// data gesetzt -> CustomPage editieren
	@Test
	public void testSaveEditSuccess() {
		pageBean.setCustomPage(anotherPage);// init emulieren
		Whitebox.setInternalState(pageBean, "data", "99");
		Whitebox.setInternalState(pageBean, "type", "show");

		when(session.isLoggedIn()).thenReturn(true);
		when(customPageDao.getById(99)).thenReturn(customPage);
		when(customPage.getUser()).thenReturn(user);
		when(customPage.getTitle()).thenReturn("Title");
		when(customPage.getContent()).thenReturn("Content");
		when(customPage.getDate()).thenReturn(new java.util.Date());
		doNothing().when(customPage).saveToHistory(any());
		doNothing().when(customPage).apply(any(), any());
		doNothing().when(customPageDao).update(any());
		doNothing().when((AbstractController) pageBean).addMessage(any());

		pageBean.save(customPage);

		verify(customPageDao).getById(99);
		verify(customPageDao, never()).create(any());// niemals in saveEdit
		verify((AbstractController) pageBean).addMessage(any());
	}

	// success CustomPage gefunden; keine Shareables zu entfernen
	@Test
	public void testRemoveSuccess() throws DuplicateUsernameException, DuplicateEmailException {
		pageBean.setUser(user);
		pageBean.setCustomPage(customPage);

		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when(userDao).update(user);
		doNothing().when(customPageDao).delete(customPage);
		doNothing().when((AbstractController) pageBean).addMessage(any());

		pageBean.remove(customPage);

		verify(customPageDao).delete(customPage);
		verify((AbstractController) pageBean).addMessage(any());
	}

	// CustomPage null
	@Test
	public void testRemoveNullFail() throws DuplicateUsernameException, DuplicateEmailException {
		pageBean.setUser(user);
		pageBean.setCustomPage(customPage);
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());

		pageBean.remove(null);

		verify(userDao, never()).update(any());
		verify(customPageDao, never()).delete(customPage);
		verify((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());
	}

	// Not logged in remove
	@Test
	public void testRemoveFail() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);

		pageBean.remove(customPage);

		verify(customPageDao, never()).delete(customPage);
	}

	// valide daten/zustand -> erfolgreich
	@Test
	public void testApplyHistorySuccess() {
		when(session.isLoggedIn()).thenReturn(true);
		when(customPage.getTitle()).thenReturn("Neuer Titel");
		when(customPage.getContent()).thenReturn("Neuer Content");
		when(customPage.getDate()).thenReturn(new java.util.Date());
		doNothing().when(customPage).saveToHistory(any());
		doNothing().when(customPage).applyHistory(any());
		doNothing().when(customPageDao).update(customPage);
		doNothing().when((AbstractController) pageBean).addMessage(any());

		pageBean.applyHistory(customPage, customPageHistory);

		verify(customPage).saveToHistory(any());
		verify(customPage).applyHistory(customPageHistory);
		verify(customPageDao).update(customPage);
		verify((AbstractController) pageBean).addMessage(any());
	}

	// nicht eingeloggt
	@Test
	public void testApplyHistoryNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		pageBean.applyHistory(customPage, customPageHistory);

		verify(customPage, never()).saveToHistory(any());
	}

	// null als CustomPage
	@Test
	public void testApplyHistoryNullCustomPageFail() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());

		// wirft IllegalArgument und fängt sie
		pageBean.applyHistory(null, customPageHistory);

		verify(customPage, never()).saveToHistory(any());
		verify((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());
	}

	// null als CustomPageHistory
	@Test
	public void testApplyHistoryNullCustomPageHistoryFail() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());

		// wirft IllegalArgument und fängt sie
		pageBean.applyHistory(customPage, null);

		verify(customPage, never()).saveToHistory(any());
		verify((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());
	}

	// valide parameter/Zustand ->removeHistory erfolgreich
	@Test
	public void testRemoveHistorySuccess() throws DuplicateUsernameException, DuplicateEmailException {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when(customPage).removeHistory(customPageHistory);
		doNothing().when(customPageDao).update(any());
		doNothing().when(userDao).update(any());
		doNothing().when((AbstractController) pageBean).addMessage(any());

		pageBean.removeHistory(customPage, customPageHistory);

		verify(customPageDao).update(any());
		verify((AbstractController) pageBean).addMessage(any());
	}

	// null als CustomPage
	@Test
	public void testRemoveNullCustomPageFail() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());

		// wirft IllegalArgument und fängt sie
		pageBean.removeHistory(null, customPageHistory);

		verify(customPage, never()).saveToHistory(any());
		verify((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());
	}

	// null als CustomPageHistory
	@Test
	public void testRemoveNullCustomPageHistoryFail() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());

		// wirft IllegalArgument und fängt sie
		pageBean.removeHistory(customPage, null);

		verify(customPage, never()).saveToHistory(any());
		verify((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());
	}

	// nicht eingeloggt
	@Test
	public void testRemoveNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		pageBean.removeHistory(customPage, customPageHistory);

		verify(customPage, never()).saveToHistory(any());
	}

	// valide parameter/Zustand ->addShareable erfolgreich
	@Test
	public void testAddShareableSuccess() throws DuplicateUsernameException, DuplicateEmailException {
		ArrayList<String> recList = new ArrayList<>();
		recList.add("admin@offline.de");
		recList.add("einegruppe");
		pageBean.setSelectedReceivers(recList);

		when(session.isLoggedIn()).thenReturn(true);
		when(customPage.getUser()).thenReturn(user);// equals
		when(userDao.getUserByEmail("admin@offline.de")).thenReturn(user);
		when(userDao.getUserByEmail("einegruppe")).thenReturn(null);
		when(groupDao.getGroupByName("einegruppe")).thenReturn(group);
		when(user.addSharedPage(customPage)).thenReturn(true);
		when(group.addSharedPage(customPage)).thenReturn(true);
		when(customPage.addSub(any())).thenReturn(true);
		when(customPage.addGroup(any())).thenReturn(true);
		doNothing().when(customPageDao).update(any());
		doNothing().when(userDao).update(any());
		doNothing().when(groupDao).update(any());

		doNothing().when((AbstractController) pageBean).addMessage(any());

		pageBean.addShareable(customPage);

		verify(customPage).addSub(any());
		verify(customPage).addGroup(any());
		verify(userDao).update(any());
		verify(groupDao).update(any());
		verify(userDao).getUserByEmail("admin@offline.de");
		verify(groupDao).getGroupByName("einegruppe");
		verify(customPageDao, times(2)).update(any());
		verify(userDao).update(any());
		verify((AbstractController) pageBean).addMessage(any());
	}

	// null CustomPage
	@Test
	public void testAddShareableNullFail() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());

		// wirft IllegalArgument und fängt sie
		pageBean.addShareable(null);

		verify(customPage, never()).saveToHistory(any());
		verify((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());
	}

	// CustomPage die einem nicht gehört -> IllegalArgumentException
	@Test
	public void testAddShareableCustomPageNotOwnedFail() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());
		when(customPage.getUser()).thenReturn(anotherUser);// equals

		// wirft IllegalArgument und fängt sie
		pageBean.addShareable(customPage);

		verify(userDao, never()).getUserByEmail(any());
		verify(groupDao, never()).getGroupByName(any());
		verify((AbstractController) pageBean, never()).addMessage(any());// never
																			// success
		verify((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());// one
																								// fail
	}

	// nicht eingeloggt
	@Test
	public void testAddShareableNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		pageBean.addShareable(customPage);

		verify(userDao, never()).getUserByEmail(any());
		verify(groupDao, never()).getGroupByName(any());
		verify((AbstractController) pageBean, never()).addMessage(any());// never
																			// success
	}

	// valide parameter/Zustand ->addShareable erfolgreich
	@Test
	public void testRemoveShareableSuccess() throws DuplicateUsernameException, DuplicateEmailException {
		when(session.isLoggedIn()).thenReturn(true);
		when(customPage.removeShareable(anotherUser)).thenReturn(true);
		when(anotherUser.removeSharedPage(customPage)).thenReturn(true);
		doNothing().when(customPageDao).update(customPage);
		when(anotherUser.getType()).thenReturn("");
		doNothing().when((AbstractController) pageBean).addMessage(any());

		pageBean.removeShareable(customPage, anotherUser);

		verify(groupDao, never()).update(any());
		verify(userDao).update(any());
		verify((AbstractController) pageBean).addMessage(any());
	}

	// null CustomPage
	@Test
	public void testRemoveShareableNullCustomPageFail() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());

		// wirft IllegalArgument und fängt sie
		pageBean.removeShareable(null, anotherUser);

		verify((AbstractController) pageBean, never()).addMessage(any());
		verify((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());
	}

	// null CustomPage
	@Test
	public void testRemoveShareableNullShareableFail() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());

		// wirft IllegalArgument und fängt sie
		pageBean.removeShareable(customPage, null);

		verify((AbstractController) pageBean, never()).addMessage(any());
		verify((AbstractController) pageBean).addMessageWithLogging(any(), any(), any(), any());
	}

	// nicht eingeloggt
	@Test
	public void testRemoveShareableNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		// direkt zum return
		pageBean.removeShareable(customPage, anotherUser);

		verify((AbstractController) pageBean, never()).addMessage(any());// never
																			// success
		verify((AbstractController) pageBean, never()).addMessageWithLogging(any(), any(), any(), any());// never
																											// fail
	}
}
