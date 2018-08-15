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
import static org.mockito.Mockito.*;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.model.Question;
import de.unibremen.gradelog.persistence.QuestionDAO;

/*
 * @author Christopher Wojtkow
 */
@RunWith(MockitoJUnitRunner.class)
public class QuestionControllerTest {

	@InjectMocks
	@Spy
	private QuestionController questionBean;

	@Mock
	private QuestionDAO questionDao;

	@Mock
	private Session session;

	@Mock
	private User user;

	@Mock
	private Question question;

	public QuestionControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(questionBean, "questionDao", questionDao);
		Whitebox.setInternalState(questionBean, "question", question);
		Whitebox.setInternalState(questionBean, "session", session);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSaveSuccess() {

		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when(questionDao).create(question);
		doNothing().when((AbstractController) questionBean).addMessage("questionCreated");

		questionBean.save();

		assertEquals("question wurde durch init nicht neu gesetzt", new Question(), questionBean.getQuestion());
		verify((AbstractController) questionBean).addMessage("questionCreated");
	}

	// nicht eingeloggt
	@Test
	public void testSaveNotLoggedInFail() {

		when(session.isLoggedIn()).thenReturn(false);

		questionBean.save();

		verify(questionDao, never()).create(question);
		assertEquals("question wurde verändert", question, questionBean.getQuestion());
	}

	@Test
	public void testSaveParameterSuccess() {

		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when(questionDao).create(question);
		doNothing().when((AbstractController) questionBean).addMessage("questionCreated");

		questionBean.save(question);

		verify(questionDao).update(question);
		verify((AbstractController) questionBean).addMessage("questionCreated");
	}

	@Test
	public void testSaveParameterNullFail() {

		when(session.isLoggedIn()).thenReturn(true);

		questionBean.save(null);

		verify(questionDao, never()).update(question);
	}

	// nicht eingeloggt
	@Test
	public void testSaveParameterNotLoggedInFail() {

		when(session.isLoggedIn()).thenReturn(false);

		questionBean.save(question);

		verify(questionDao, never()).update(question);
	}

	@Test
	public void testRemoveSuccess() {

		when(session.isLoggedIn()).thenReturn(true);
		when(questionBean.getSelectedQuestion()).thenReturn(question);
		doNothing().when(questionDao).delete(question);
		doNothing().when((AbstractController) questionBean).addMessage("questionDeleted");

		questionBean.remove();

		verify(questionDao).update(question);
		verify((AbstractController) questionBean).addMessage("questionDeleted");

	}

	// nicht eingeloggt
	@Test
	public void testRemoveNotLoggedInFail() {

		when(session.isLoggedIn()).thenReturn(false);

		questionBean.remove();

		verify(questionDao, never()).update(question);
	}

	@Test
	public void testRemoveParameterSuccess() {

		when(session.isLoggedIn()).thenReturn(true);
		doNothing().when(questionDao).delete(question);
		doNothing().when((AbstractController) questionBean).addMessage("questionDeleted");

		questionBean.remove(question);

		verify(questionDao).update(question);
		verify((AbstractController) questionBean).addMessage("questionDeleted");

	}

	@Test
	public void testRemoveParameterNullFail() {

		when(session.isLoggedIn()).thenReturn(true);

		questionBean.remove(null);

		verify(questionDao, never()).update(question);
	}

	// nicht eingeloggt
	@Test
	public void testRemoveParameterNotLoggedInFail() {

		when(session.isLoggedIn()).thenReturn(false);

		questionBean.remove(question);

		verify(questionDao, never()).update(question);
	}

}