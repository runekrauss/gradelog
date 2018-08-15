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

import javax.faces.event.ActionEvent;

import java.util.Date;
import java.util.HashSet;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.Timetable;
import de.unibremen.gradelog.model.TimetableEntry;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.model.Group;
import de.unibremen.gradelog.persistence.TimetableDAO;
import de.unibremen.gradelog.persistence.TimetableEntryDAO;

/*
 * @author Christopher Wojtkow
 */
@RunWith(MockitoJUnitRunner.class)
public class TimetableControllerTest {

	@InjectMocks
	@Spy
	private TimetableController timetableBean;
	@Mock
	private TimetableDAO timetableDao;

	@Mock
	private TimetableEntryDAO timetableEntryDao;

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
	private TimetableEntry timetableEntry;

	@Mock
	private Timetable timetable;

	@Mock
	private ActionEvent actionEvent;

	@Mock
	private HashSet<TimetableEntry> what_am_i_testing_jpeg;

	@Mock
	private Date startDate;

	@Mock
	private Date startTime;

	@Mock
	private Date endTime;

	public TimetableControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(timetableBean, "timetableDao", timetableDao);
		Whitebox.setInternalState(timetableBean, "timetableEntryDao", timetableEntryDao);
		Whitebox.setInternalState(timetableBean, "session", session);
		Whitebox.setInternalState(timetableBean, "timetable", timetable);
		Whitebox.setInternalState(timetableBean, "startDate", startDate);
	}

	@After
	public void tearDown() {
	}

	// starttime < endttime
	@Test
	public void testAddTimetableEntrySuccess() {
		Whitebox.setInternalState(timetableBean, "startTime", startTime);
		Whitebox.setInternalState(timetableBean, "endTime", endTime);
		Whitebox.setInternalState(timetableBean, "timetableEntry", timetableEntry);

		when(session.isLoggedIn()).thenReturn(true);
		when(startTime.getTime()).thenReturn(new Date().getTime());
		when(endTime.getTime()).thenReturn(new Date().getTime() + 3600000); // startime
																			// earlier
																			// than
																			// endtime

		when(timetable.getEntries()).thenReturn(what_am_i_testing_jpeg);
		when(what_am_i_testing_jpeg.contains(timetableEntry)).thenReturn(false);
		doNothing().when(timetableEntryDao).create(timetableEntry);
		doNothing().when(timetable).addEntry(timetableEntry);
		when(startDate.getTime()).thenReturn((long) 0);
		doNothing().when(timetableEntry).setStartTime(any());
		doNothing().when(timetableEntry).setEndTime(any());
		doNothing().when(timetableEntryDao).update(timetableEntry);
		doNothing().when(timetableDao).update(timetable);
		doNothing().when((AbstractController) timetableBean).addMessage("successEntryAdded");

		when(session.getUser()).thenReturn(user);
		when(user.getTimetable()).thenReturn(timetable);

		timetableBean.addTimetableEntry(actionEvent);

		verify(timetableEntryDao).create(timetableEntry);
		verify(timetable).addEntry(timetableEntry);
		verify((AbstractController) timetableBean, never()).addMessage("errorInvalidTimetableTime");
		verify((AbstractController) timetableBean).addMessage("successEntryAdded");
	}

	// starttime > endttime
	@Test
	public void testAddTimetableEntryFail() {
		Whitebox.setInternalState(timetableBean, "startTime", startTime);
		Whitebox.setInternalState(timetableBean, "endTime", endTime);
		Whitebox.setInternalState(timetableBean, "timetableEntry", timetableEntry);

		when(session.isLoggedIn()).thenReturn(true);
		when(startTime.getTime()).thenReturn(new Date().getTime() + 3600000);
		when(endTime.getTime()).thenReturn(new Date().getTime()); // startime
																	// earlier
																	// than
																	// endtime
		doNothing().when((AbstractController) timetableBean).addMessage("errorInvalidTimetableTime");

		timetableBean.addTimetableEntry(actionEvent);

		verify(timetableEntryDao, never()).create(timetableEntry);
		verify(timetable, never()).addEntry(timetableEntry);
		verify((AbstractController) timetableBean).addMessage("errorInvalidTimetableTime");
		verify((AbstractController) timetableBean, never()).addMessage("successEntryAdded");
	}

	// not logged in
	@Test
	public void testAddTimetableEntryNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		timetableBean.addTimetableEntry(actionEvent);

		verify(timetableEntry, never()).setStartTime(any());
	}

	@Test
	public void testRemoveTimetableEntrySuccess() {
		Whitebox.setInternalState(timetableBean, "timetableEntry", timetableEntry);
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when(timetable).removeEntry(timetableEntry);
		doNothing().when(timetableDao).update(timetable);
		doNothing().when(timetableEntryDao).update(timetableEntry);
		doNothing().when((AbstractController) timetableBean).addMessage("successEntryDeleted");

		timetableBean.removeTimetableEntry(actionEvent);

		verify((AbstractController) timetableBean).addMessage("successEntryDeleted");
	}

	// not logged in
	@Test
	public void testRemoveTimetableEntryNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		timetableBean.removeTimetableEntry(actionEvent);

		verify(timetableDao, never()).update(any());
		verify(timetableEntryDao, never()).delete(any());
	}
}