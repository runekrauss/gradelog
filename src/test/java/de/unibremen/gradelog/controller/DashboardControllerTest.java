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
import org.primefaces.event.DashboardReorderEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.persistence.CustomPageDAO;
import org.primefaces.model.DefaultDashboardModel;

/*
 * @author Rune Krauss
 */
@RunWith(MockitoJUnitRunner.class)
public class DashboardControllerTest {

	@InjectMocks
	@Spy
	private DashboardController dashboardBean;

	@Mock
	private DefaultDashboardModel model;

	@Mock
	private UserDAO userDAO;

	@Mock
	private CustomPageDAO customPageDAO;

	@Mock
	private User user;

	@Mock
	private List<String> widgets;

	@Mock
	private Session session;

	@Mock
	private DashboardReorderEvent event;

	public DashboardControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(dashboardBean, "model", model);
		Whitebox.setInternalState(dashboardBean, "userDAO", userDAO);
		Whitebox.setInternalState(dashboardBean, "user", user);
		Whitebox.setInternalState(dashboardBean, "widgets", widgets);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void handleReorderSuccess() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) dashboardBean).addMessage(any());
		when(event.getWidgetId()).thenReturn("todos");
		when(event.getItemIndex()).thenReturn(1);

		dashboardBean.handleReorder(event);
		verify(userDAO).update(user);
	}
	
	@Test
	public void handleReorderWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) dashboardBean).addMessage(any());

		dashboardBean.handleReorder(event);
		verify(userDAO, never()).update(user);
	}
}
