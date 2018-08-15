package de.unibremen.gradelog.controller;

import de.unibremen.gradelog.persistence.CustomPageDAO;
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

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.Group;
import de.unibremen.gradelog.model.GroupInvite;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.persistence.GroupDAO;
import de.unibremen.gradelog.persistence.GroupInviteDAO;

/*
 * @author Christopher Wojtkow
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupControllerTest {

	@InjectMocks
	@Spy
	private GroupController groupBean;

	@Mock
	private GroupDAO groupDao;

	@Mock
	private GroupInviteDAO groupInviteDao;

	@Mock
	private CustomPageDAO customPageDAO;

	@Mock
	private UserDAO userDao;

	@Mock
	private Session session;

	@Mock
	private User user;

	@Mock
	private HashSet<User> users;

	@Mock
	private User anotherUser;

	@Mock
	private Group group;

	@Mock
	private GroupInvite inv;

	@Mock
	private HashSet<GroupInvite> groupinvs;

	@Mock
	private HashSet<Group> groups;

	@Mock
	private Group selectedGroup;

	public GroupControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(groupBean, "groupDao", groupDao);
		Whitebox.setInternalState(groupBean, "groupInviteDao", groupInviteDao);
		Whitebox.setInternalState(groupBean, "userDao", userDao);
		Whitebox.setInternalState(groupBean, "session", session);
		Whitebox.setInternalState(groupBean, "group", group);
	}

	@After
	public void tearDown() {
	}

	// eingeloggt
	// leere liste von receivern in addInvites
	// -> Success
	@Test
	public void testSaveSuccess() throws DuplicateUsernameException, DuplicateEmailException {
		when(session.isLoggedIn()).thenReturn(true);
		when(group.getName()).thenReturn("gruppenname");
		when(groupDao.getGroupByName("gruppenname")).thenReturn(null);// keine
																		// gruppe
																		// gefunden
																		// ->
																		// feuer
																		// frei
		when(session.getUser()).thenReturn(user);
		doNothing().when(groupDao).create(group);
		when(user.getGroups()).thenReturn(groups);
		when(groups.add(group)).thenReturn(true);
		doNothing().when(group).addUser(user);
		doNothing().when(userDao).update(user);
		doNothing().when(groupDao).update(group);
		doNothing().when(groupBean).setSelectedGroup(group);

		List<String> returnedUsers = new ArrayList<>();

		Whitebox.setInternalState(groupBean, "returnedUsers", returnedUsers);

		doNothing().when((AbstractController) groupBean).addMessage("sucessfullGroupCreate");
		doNothing().when((AbstractController) groupBean).addMessage("successfullUserInvite");

		groupBean.save();

		assertEquals(new Group(), groupBean.getGroup());
		assertEquals(new ArrayList<String>(), groupBean.getReturnedUsers());
		assertEquals(new Group(), groupBean.getGroup());
	}

	@Test
	public void testSaveGroupDoesntExistFail() {
		when(session.isLoggedIn()).thenReturn(true);
		when(group.getName()).thenReturn("gruppenname");
		when(groupDao.getGroupByName("gruppenname")).thenReturn(group);// irgeneine
																		// gruppe
																		// gefunden
																		// ->
																		// abbruch!
		doNothing().when((AbstractController) groupBean).addMessage("groupNameIsDoubled");

		groupBean.save();

		verify(groupDao, never()).create(group);
		verify((AbstractController) groupBean).addMessage("groupNameIsDoubled");
		verify((AbstractController) groupBean, never()).addMessage("sucessfullGroupCreate");
	}

	@Test
	public void testSaveNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		groupBean.save();

		verify(groupDao, never()).create(group);
	}

	@Test
	public void testUpdateDeleteSuccess() throws DuplicateUsernameException, DuplicateEmailException {
		when(session.isLoggedIn()).thenReturn(true);
		when(session.getUser()).thenReturn(user);
		when(groupBean.getSelectedGroup()).thenReturn(group);
		doNothing().when(user).removeGroup(group);
		doNothing().when(group).removeUser(user);
		doNothing().when(userDao).update(user);
		doNothing().when(groupDao).update(group);
		// cleanup gruppe
		when(group.getUsers()).thenReturn(users);
		when(users.isEmpty()).thenReturn(true);

		ArrayList<GroupInvite> inviteList = new ArrayList<>();
		inviteList.add(inv);

		when(groupInviteDao.getGroupInvites(group)).thenReturn(inviteList);
		when(inv.getUser()).thenReturn(anotherUser);

		HashSet<GroupInvite> anotherInviteSet = new HashSet<>();
		anotherInviteSet.add(inv);

		when(anotherUser.getInvites()).thenReturn(anotherInviteSet);
		doNothing().when(groupInviteDao).delete(inv);
		doNothing().when(userDao).update(user);
		doNothing().when(groupDao).delete(group);// userDao schon oben geklärt
		doNothing().when((AbstractController) groupBean).addMessage("successfullGroupLeave");
		doNothing().when((AbstractController) groupBean).addMessage("successfullGroupDelete");

		groupBean.updateDelete();

		verify(session, times(2)).isLoggedIn();// in updateDelete() und remove()
		verify(userDao, times(2)).update(any());// in updateDelete() oben und im
												// for-invite
		verify(groupDao).update(any());
		verify(groupInviteDao).delete(inv);// genau einmal
		verify((AbstractController) groupBean).addMessage("successfullGroupLeave");
	}

	@Test
	public void testUpdateDeleteNotLoggedInFail() throws DuplicateUsernameException, DuplicateEmailException {
		when(session.isLoggedIn()).thenReturn(false);

		groupBean.updateDelete();

		verify(userDao, never()).update(any());
		verify(groupDao, never()).update(any());
		verify(groupInviteDao, never()).delete(inv);
		verify((AbstractController) groupBean, never()).addMessage("successfullGroupLeave");
	}

	// eingeloggt, nicht leere liste von receivern
	@Test
	public void testAddInvitesSuccess() throws DuplicateUsernameException, DuplicateEmailException {
		List<String> returnedUsers = new ArrayList<>();
		returnedUsers.add("admin2@offline.de");
		returnedUsers.add("nichtvorhanden");

		Whitebox.setInternalState(groupBean, "returnedUsers", returnedUsers);

		when(session.isLoggedIn()).thenReturn(true);
		when(userDao.getUserByEmail("admin2@offline.de")).thenReturn(anotherUser);
		when(userDao.getUserByEmail("admin2@nichtvorhanden.de")).thenReturn(null);
		when(groupBean.getSelectedGroup()).thenReturn(group);
		when(session.getUser()).thenReturn(user);
		when(anotherUser.getInvites()).thenReturn(groupinvs);
		when(groupinvs.contains(any())).thenReturn(false);
		doNothing().when(groupInviteDao).create(any());
		doNothing().when(anotherUser).addInvite(any());
		doNothing().when(userDao).update(anotherUser);
		doNothing().when((AbstractController) groupBean).addMessage("successfullUserInvite");

		groupBean.addInvites();

		verify(groupBean).getSelectedGroup();
		verify(groupInviteDao).create(any());
		verify(anotherUser).addInvite(any());
		verify(userDao).update(anotherUser);
		verify((AbstractController) groupBean).addMessage("successfullUserInvite");
	}

	// nicht eingeloggt -> Fail
	@Test
	public void testAddInvitesNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		groupBean.addInvites();

		verify((AbstractController) groupBean, never()).addMessage("successfullUserInvite");
	}

	@Test
	public void testRemoveSuccess() {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when(groupDao).delete(group);
		doNothing().when((AbstractController) groupBean).addMessage("successfullGroupDelete");

		groupBean.remove(group);

		verify((AbstractController) groupBean).addMessage("successfullGroupDelete");

	}

	// null parameter
	@Test
	public void testRemoveNullFail() {
		when(session.isLoggedIn()).thenReturn(true);

		groupBean.remove(null);

		verify((AbstractController) groupBean, never()).addMessage("successfullGroupDelete");
	}

	// nicht eingeloggt
	@Test
	public void testRemoveNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		groupBean.remove(null);

		verify((AbstractController) groupBean, never()).addMessage("successfullGroupDelete");

	}
}