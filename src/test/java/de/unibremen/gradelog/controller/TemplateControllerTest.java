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

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.Template;
import de.unibremen.gradelog.persistence.SessionDAO;
import de.unibremen.gradelog.persistence.TemplateDAO;
import de.unibremen.gradelog.persistence.UserDAO;

/*
 * @author Rune Krauss
 */
@RunWith(MockitoJUnitRunner.class)
public class TemplateControllerTest {

	@InjectMocks
	@Spy
	private TemplateController templateBean;

	@Mock
	private TemplateDAO templateDAO;

	@Mock
	private UserDAO userDAO;

	@Mock
	private SessionDAO sessionDAO;

	@Mock
	private Session session;

	@Mock
	Template backend;

	@Mock
	Template frontend;

	public TemplateControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(templateBean, "templateDAO", templateDAO);
		Whitebox.setInternalState(templateBean, "userDAO", userDAO);
		Whitebox.setInternalState(templateBean, "backendTemplate", backend);
		Whitebox.setInternalState(templateBean, "frontendTemplate", frontend);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSaveBackendTemplateSuccess() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) templateBean).addMessage(any());
		when(backend.getTitle()).thenReturn("");
		when(backend.getMaintenanceContent()).thenReturn("");

		templateBean.saveBackend();
		verify(templateDAO).update(backend);
	}

	@Test
	public void testSaveBackendTemplateWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) templateBean).addMessage(any());

		templateBean.saveBackend();
		verify(templateDAO, never()).update(backend);
	}

	@Test
	public void testSaveFrontendTemplateSuccess() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when((AbstractController) templateBean).addMessage(any());
		when(frontend.getTitle()).thenReturn("");
		when(frontend.getMaintenanceContent()).thenReturn("");
		when(frontend.getIntro()).thenReturn("");
		when(frontend.getSlogan()).thenReturn("");
		when(frontend.getCopyright()).thenReturn("");

		templateBean.saveFrontend();
		verify(templateDAO).update(frontend);
	}

	@Test
	public void testSaveFrontendTemplateWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		doNothing().when((AbstractController) templateBean).addMessage(any());

		templateBean.saveFrontend();
		verify(templateDAO, never()).update(frontend);
	}
}
