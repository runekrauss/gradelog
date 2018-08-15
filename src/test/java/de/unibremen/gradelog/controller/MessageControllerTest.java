package de.unibremen.gradelog.controller;

import de.unibremen.gradelog.model.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
import java.util.HashSet;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.persistence.GroupDAO;
import de.unibremen.gradelog.persistence.MessageDAO;

/*
 * @author Christopher Wojtkow
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageControllerTest {

	@InjectMocks
	@Spy
	private MessageController messageBean;

	@Mock
	private MessageDAO messageDao;

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
	private Profile profile;

	@Mock
	private User anotherUser;

	@Mock
	private Group group;

	@Mock
	private Message message;

	@Mock
	private Message anotherMessage;

	public MessageControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(messageBean, "messageDao", messageDao);
		Whitebox.setInternalState(messageBean, "userDao", userDao);
		Whitebox.setInternalState(messageBean, "groupDao", groupDao);
		Whitebox.setInternalState(messageBean, "session", session);
		Whitebox.setInternalState(messageBean, "message", message);
		Whitebox.setInternalState(messageBean, "user", user);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSaveSuccess() throws DuplicateUsernameException, DuplicateEmailException {

		ArrayList<String> receivers = new ArrayList<>();
		receivers.add("Pflaumen Gruppe");
		receivers.add("admin@offline.de");
		Whitebox.setInternalState(messageBean, "selectedReceivers", receivers);
		HashSet<Message> allMessages = new HashSet<>();
		allMessages.add(message);
		ArrayList<User> users = new ArrayList<>();
		users.add(user);
		users.add(anotherUser);

		when(session.isLoggedIn()).thenReturn(true);
		when(message.getContent()).thenReturn("Test");
		when(session.getUser()).thenReturn(user);
		when(user.getProfile()).thenReturn(profile);
		when(profile.getAboutMe()).thenReturn("Test");
		when(user.getEmail()).thenReturn("admin@offline.de");
		doNothing().when((AbstractController) messageBean).addMessage(any());
		doNothing().when(message).setAuthor("admin@offline.de");
		doNothing().when(message).setReceivers(any());
		doNothing().when(messageDao).create(message);
		when(user.addMessage(message)).thenReturn(null);
		when(userDao.getUserByEmail("Pflaumen Gruppe")).thenReturn(null);
		when(userDao.getUserByEmail("admin@offline.de")).thenReturn(user);
		when(groupDao.getGroupByName("Pflaumen Gruppe")).thenReturn(group);

		HashSet<User> userSet = new HashSet<User>();
		userSet.add(anotherUser);
		HashSet<User> userSet2 = new HashSet<User>();
		userSet.add(user);

		when(group.getUserSet()).thenReturn(userSet);
		when(user.getUserSet()).thenReturn(userSet2);
		// when(user.addMessage(message)).thenReturn(null);// schon oben
		when(anotherUser.addMessage(message)).thenReturn(null);
		doNothing().when(userDao).update(any());
		// init call mocks
		when(user.getMessages()).thenReturn(allMessages);
		doNothing().when(messageDao).update(message);
		when(userDao.getAllUsers()).thenReturn(users);
		// init call end
		// when(user.getMessages()).thenReturn("admin@offline.de");

		messageBean.save();

		// jede menge verify hier

		verify(userDao).getUserByEmail("Pflaumen Gruppe");
		verify(userDao).getUserByEmail("admin@offline.de");
		verify(groupDao).getGroupByName("Pflaumen Gruppe");
		verify(userDao, times(2)).update(user);
		// nach init
		verify(user).getMessages();
		verify(userDao).getAllUsers();
		ArrayList<User> testList = new ArrayList<>();
		testList.add(user);
		testList.add(anotherUser);
		assertEquals("allUsers sollte eigentlich die liste aller User halten", testList, messageBean.getAllUsers());
		assertEquals("SelectedReceivers haette leer sein sollen nach dem init cal zum schluss von save",
				new ArrayList<String>(), messageBean.getSelectedReceivers());
	}

	// nicht eingeloggt -> fail
	@Test
	public void testSaveNotLoggedInFail() throws DuplicateUsernameException, DuplicateEmailException {
		when(session.isLoggedIn()).thenReturn(false);

		messageBean.save();

		verify(userDao, never()).update(any());
		verify(messageDao, never()).create(any());
	}

	@Test
	public void testSaveParameterSuccess() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when(messageDao).update(message);

		messageBean.save(message);

		verify(messageDao).update(message);
	}

	// nicht eingeloggt -> fail
	@Test
	public void testSaveParameterNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when(messageDao).update(message);

		messageBean.save(message);

		verify(messageDao, never()).update(message);
	}

	// null als parameter -> fail
	@Test
	public void testSaveParameterNullFail() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when(messageDao).update(message);

		messageBean.save(null);

		verify(messageDao, never()).update(message);
	}

	@Test
	public void testRemoveSuccess() {
		when(session.isLoggedIn()).thenReturn(true);

		messageBean.remove(message);

		verify(messageDao).delete(message);
	}

	// nicht eingeloggt -> fail
	@Test
	public void testRemoveNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		messageBean.remove(message);

		verify(messageDao, never()).delete(message);
	}

	// null als parameter -> fail
	@Test
	public void testRemoveNullFail() {
		when(session.isLoggedIn()).thenReturn(true);

		messageBean.remove(null);

		verify(messageDao, never()).delete(message);
	}

}