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
import org.primefaces.event.ScheduleEntryMoveEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Set;

import javax.faces.event.ActionEvent;

import de.unibremen.gradelog.model.Calendar;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.Task;
import de.unibremen.gradelog.persistence.CalendarDAO;
import de.unibremen.gradelog.persistence.SessionDAO;
import de.unibremen.gradelog.persistence.TaskDAO;

/*
 * @author Rune Krauss
 */
@RunWith(MockitoJUnitRunner.class)
public class CalendarControllerTest {

	@InjectMocks
	@Spy
	private CalendarController calendarBean;

	@Mock
	private CalendarDAO calendarDAO;

	@Mock
	private TaskDAO todoDAO;

	@Mock
	private SessionDAO sessionDAO;

	@Mock
	private ActionEvent event;

	@Mock
	private ScheduleEntryMoveEvent moveEvent;

	@Mock
	private Session session;

	@Mock
	private Calendar calendar;

	@Mock
	private Task todo;

	@Mock
	private Set<Task> todos;

	@Mock
	private java.util.Date startTime;

	@Mock
	private java.util.Date endTime;

	public CalendarControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(calendarBean, "taskDAO", todoDAO);
		Whitebox.setInternalState(calendarBean, "calendarDAO", calendarDAO);
		Whitebox.setInternalState(calendarBean, "calendar", calendar);
		Whitebox.setInternalState(calendarBean, "task", todo);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testUpdateTodoSuccess() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) calendarBean).addMessage(any());
		when(calendar.getTasks()).thenReturn(todos);
		when(todos.contains(todo)).thenReturn(true);
		when(todo.getStartTime()).thenReturn(startTime);
		when(startTime.getTime()).thenReturn((long) 1);
		when(todo.getEndTime()).thenReturn(endTime);
		when(endTime.getTime()).thenReturn((long) 1);

		calendarBean.addTask(event);
		verify(calendarDAO).update(calendar);
		verify(todoDAO).update(todo);
	}

	@Test
	public void testStartTimeIsLargerThanEndTime() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) calendarBean).addMessage(any());
		when(calendar.getTasks()).thenReturn(todos);
		when(todos.contains(todo)).thenReturn(true);
		when(todo.getStartTime()).thenReturn(startTime);
		when(startTime.getTime()).thenReturn((long) 2);
		when(todo.getEndTime()).thenReturn(endTime);
		when(endTime.getTime()).thenReturn((long) 1);

		calendarBean.addTask(event);
		verify(calendarDAO, never()).update(calendar);
		verify(todoDAO, never()).update(todo);
	}

	@Test
	public void testAddTodoWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) calendarBean).addMessage(any());

		calendarBean.addTask(event);
		verify(calendarDAO, never()).update(calendar);
		verify(todoDAO, never()).update(todo);
	}

	@Test
	public void testCreateTodo() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) calendarBean).addMessage(any());
		when(calendar.getTasks()).thenReturn(todos);
		when(todos.contains(todo)).thenReturn(false);
		when(calendar.addTask(todo)).thenReturn(null);
		when(todo.getStartTime()).thenReturn(startTime);
		when(startTime.getTime()).thenReturn((long) 1);
		when(todo.getEndTime()).thenReturn(endTime);
		when(endTime.getTime()).thenReturn((long) 1);

		calendarBean.addTask(event);
		verify(todoDAO).create(todo);
		verify(calendarDAO).update(calendar);
		verify(todoDAO).update(todo);
	}

	@Test
	public void testRemoveTodoSuccess() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) calendarBean).addMessage(any());
		when(calendar.removeTask(todo)).thenReturn(null);

		calendarBean.removeTask(event);
		verify(calendarDAO).update(calendar);
		verify(todoDAO).delete(todo);
	}

	@Test
	public void testRemoveTodoWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) calendarBean).addMessage(any());

		calendarBean.removeTask(event);
		verify(calendarDAO, never()).update(calendar);
		verify(todoDAO, never()).delete(todo);
	}

}
