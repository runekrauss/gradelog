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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Date;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.Missing;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.persistence.MissingDAO;

/*
 * @author Christopher Wojtkow
 */
@RunWith(MockitoJUnitRunner.class)
public class MissingControllerTest {

	@InjectMocks
	@Spy
	private MissingController missingBean;

	@Mock
	private MissingDAO missingDao;

	@Mock
	private UserDAO userDao;

	@Mock
	private Session session;

	@Mock
	private User user;

	@Mock
	private User anotherUser;

	@Mock
	private Missing missing;

	@Mock
	private Missing selectedMissing;

	@Mock
	private Date date;

	public MissingControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(missingBean, "missingDao", missingDao);
		Whitebox.setInternalState(missingBean, "userDao", userDao);
		Whitebox.setInternalState(missingBean, "session", session);
	}

	@After
	public void tearDown() {
	}

	// eingeloggt + valide daten
	@Test
	public void testCreateSuccess() throws DuplicateUsernameException, DuplicateEmailException {
		missingBean.setMissing(missing);// init
		missingBean.setSelectedMissing(selectedMissing);// init

		when(session.isLoggedIn()).thenReturn(true);
		when(missing.getStartTime()).thenReturn(date);
		when(missing.getEndTime()).thenReturn(date);
		when(date.after(any())).thenReturn(false);// branch false
		when(session.getUser()).thenReturn(user);
		doNothing().when(missing).setUser(user);
		doNothing().when(missingDao).create(missing);
		doNothing().when((AbstractController) missingBean).addMessage("successfulAddAbsence");
		when(user.addMissing(missing)).thenReturn(null);
		doNothing().when(userDao).update(user);

		missingBean.create();

		verify(missing).setUser(any());// direkt nach if block der nicht
										// aktiviert werden soll
		// verify((AbstractController) missingBean,
		// never()).addMessage(any());//Nie einen error ausgegeben
		verify(missingDao).create(missing);
		verify((AbstractController) missingBean).addMessage("successfulAddAbsence");
		verify(user).addMissing(missing);
		verify(userDao).update(user);
	}

	// invalide daten beim date
	@Test
	public void testCreateDateRangeFail() {
		missingBean.setMissing(missing);// init
		missingBean.setSelectedMissing(selectedMissing);// init

		when(session.isLoggedIn()).thenReturn(true);
		when(missing.getStartTime()).thenReturn(date);
		when(missing.getEndTime()).thenReturn(date);
		when(date.after(any())).thenReturn(true);// branch true
		doNothing().when((AbstractController) missingBean).addMessage(any());

		missingBean.create();

		verify(missing, never()).setUser(any());// direkt nach if block der
												// aktiviert werden soll
		verify((AbstractController) missingBean).addMessage(any());
	}

	// nicht eingeloggt
	@Test
	public void testCreateNotLoggedInFail() {
		missingBean.setMissing(missing);// init
		missingBean.setSelectedMissing(selectedMissing);// init

		when(session.isLoggedIn()).thenReturn(false);

		missingBean.create();

		verify(missing, never()).setUser(any());// keiner der beiden if branches
		verify((AbstractController) missingBean, never()).addMessage(any());// keiner
																			// der
																			// beiden
																			// if
																			// branches
	}

	// start time nach endtime; eingeloggt -> success
	@Test
	public void testUpdateSuccess() {
		missingBean.setMissing(missing);// init
		missingBean.setSelectedMissing(selectedMissing);// init

		when(session.isLoggedIn()).thenReturn(true);
		when(selectedMissing.getStartTime()).thenReturn(date);
		when(selectedMissing.getEndTime()).thenReturn(date);
		when(date.after(date)).thenReturn(false);// branch false
		doNothing().when(missingDao).update(selectedMissing);
		doNothing().when((AbstractController) missingBean).addMessage("successfulEdit");

		missingBean.update();

		verify(date).after(date);
		verify(missingDao).update(selectedMissing);
		verify((AbstractController) missingBean).addMessage("successfulEdit");
	}

	// start time ist nicht vor endtime -> fail
	@Test
	public void testUpdateDateRangeFail() {
		missingBean.setMissing(missing);// init
		missingBean.setSelectedMissing(selectedMissing);// init

		when(session.isLoggedIn()).thenReturn(true);
		when(selectedMissing.getStartTime()).thenReturn(date);
		when(selectedMissing.getEndTime()).thenReturn(date);
		when(date.after(date)).thenReturn(true);// branch true
		doNothing().when((AbstractController) missingBean).addMessage(any());

		missingBean.update();

		verify(date).after(date);
		verify(missingDao, never()).update(selectedMissing);// direkt nach if
															// block der
															// aktiviert werden
															// soll
		verify((AbstractController) missingBean).addMessage(any());
	}

	// nicht eingeloggt
	@Test
	public void testUpdateNotLoggedInFail() {
		missingBean.setMissing(missing);// init
		missingBean.setSelectedMissing(selectedMissing);// init

		when(session.isLoggedIn()).thenReturn(false);

		missingBean.update();

		verify(missingDao, never()).update(any());// keiner der beiden if
													// branches
		verify((AbstractController) missingBean, never()).addMessage(any());// keiner
																			// der
																			// beiden
																			// if
																			// branches
	}

	// eingeloggt
	@Test
	public void testRemoveSuccess() throws DuplicateUsernameException, DuplicateEmailException {
		missingBean.setMissing(missing);// init
		missingBean.setSelectedMissing(selectedMissing);// init

		when(session.isLoggedIn()).thenReturn(true);
		when(selectedMissing.getUser()).thenReturn(user);
		doNothing().when(missingDao).delete(selectedMissing);
		when(user.removeMissing(selectedMissing)).thenReturn(null);
		doNothing().when(userDao).update(user);
		doNothing().when((AbstractController) missingBean).addMessage("successfulRemove");

		missingBean.remove();

		verify(selectedMissing).getUser();
		verify(user).removeMissing(selectedMissing);
		verify(userDao).update(user);
		verify((AbstractController) missingBean).addMessage("successfulRemove");
	}

	// nicht eingeloggt
	@Test
	public void testRemoveNotLoggedInFail() {
		missingBean.setMissing(missing);// init
		missingBean.setSelectedMissing(selectedMissing);// init

		when(session.isLoggedIn()).thenReturn(false);

		missingBean.remove();

		verify(selectedMissing, never()).getUser();
	}

}