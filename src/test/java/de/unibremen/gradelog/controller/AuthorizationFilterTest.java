package de.unibremen.gradelog.controller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unibremen.gradelog.model.Session;

/*
 * @author Rune Krauss
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthorizationFilterTest {

	@InjectMocks
	private AuthorizationFilter filter;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain chain;

	@Mock
	private Session session;

	public AuthorizationFilterTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSchedulerAccessWithoutPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(false);
		StringBuffer buffer = new StringBuffer();
		when(request.getRequestURL()).thenReturn(buffer);
		buffer.append("/scheduler/profile/settings.xhtml");

		filter.doFilter(request, response, chain);
		verify(response).sendRedirect(any());
	}

	@Test
	public void testSchedulerAccessWithPermission() throws Exception {
		when(session.isLoggedIn()).thenReturn(true);
		StringBuffer buffer = new StringBuffer();
		when(request.getRequestURL()).thenReturn(buffer);
		buffer.append("/scheduler/profile/settings.xhtml");

		filter.doFilter(request, response, chain);
		verify(chain).doFilter(request, response);
	}
}
