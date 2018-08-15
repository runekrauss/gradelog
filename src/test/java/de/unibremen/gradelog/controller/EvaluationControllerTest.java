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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.model.Question;
import de.unibremen.gradelog.model.QuestionHolder;
import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.Evaluation;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.persistence.EvaluationDAO;
import de.unibremen.gradelog.persistence.QuestionDAO;

/*
 * @author Christopher Wojtkow
 */
@RunWith(MockitoJUnitRunner.class)
public class EvaluationControllerTest {

	@InjectMocks
	@Spy
	private EvaluationController evaluationBean;

	@Mock
	private EvaluationDAO evaluationDao;

	@Mock
	private QuestionDAO questionDao;

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
	private ArrayList<QuestionHolder> qlist;

	@Mock
	private Evaluation evaluation;

	@Mock
	private Evaluation selectedEva;

	@Mock
	private Hashtable<Question, Integer> questionTable;

	public EvaluationControllerTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		Whitebox.setInternalState(evaluationBean, "evaluationDao", evaluationDao);
		Whitebox.setInternalState(evaluationBean, "userDao", userDao);
		Whitebox.setInternalState(evaluationBean, "questionDao", questionDao);
		Whitebox.setInternalState(evaluationBean, "session", session);
		Whitebox.setInternalState(evaluationBean, "evaluation", evaluation);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSaveSuccess() throws DuplicateUsernameException, DuplicateEmailException {
		when(session.isLoggedIn()).thenReturn(true);
		when(session.getUser()).thenReturn(user);
		when(user.addEvaluation(evaluation)).thenReturn(null);
		doNothing().when(evaluation).setUser(user);
		doNothing().when(evaluation).setDate(any());
		when(evaluation.getQuestionHolders()).thenReturn(qlist);
		doNothing().when(evaluation).setEntries(qlist);
		doNothing().when(evaluationDao).create(evaluation);
		doNothing().when(userDao).update(user);
		// init
		HashSet<Evaluation> evaluationSet = new HashSet<>();
		when(user.getEvaluations()).thenReturn(evaluationSet);
		ArrayList<Question> questionSet = new ArrayList<>();
		when(questionDao.getAllQuestions()).thenReturn(questionSet);
		when(evaluation.getEntries()).thenReturn(questionTable);
		when(questionTable.put(any(), any())).thenReturn(null);
		doNothing().when(evaluation).updateQuestionHolders();
		doNothing().when((AbstractController) evaluationBean).addMessage(any());

		evaluationBean.save();

		verify(userDao).update(user);// wieso ist das zweimal im code
		verify(questionDao).getAllRemainingQuestions();
		verify(evaluation, never()).updateQuestionHolders();// zu diesem
															// Zeitpunk sollte
															// evaluation mit
															// einer neuen
															// Instanz
															// ueberschrieben
															// worden sein
															// (@init())
	}

	// not logged in
	@Test
	public void testSaveNotLoggedInFail() throws DuplicateUsernameException, DuplicateEmailException {
		when(session.isLoggedIn()).thenReturn(false);

		evaluationBean.save();

		verify(userDao, never()).update(user);
	}

	@Test
	public void testSaveParameterSuccess() {
		when(session.isLoggedIn()).thenReturn(true);

		evaluationBean.save(evaluation);

		verify(evaluationDao).update(evaluation);
	}

	// not logged in
	@Test
	public void testSaveParameterNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		evaluationBean.save(evaluation);

		verify(evaluationDao, never()).update(evaluation);
	}

	@Test
	public void testRemoveSuccess() throws DuplicateUsernameException, DuplicateEmailException {
		when(session.isLoggedIn()).thenReturn(true);
		when(session.getUser()).thenReturn(user);
		when(evaluationBean.getSelectedEvaluation()).thenReturn(selectedEva);
		when(user.removeEvaluation(selectedEva)).thenReturn(null);
		doNothing().when(userDao).update(user);
		doNothing().when((AbstractController) evaluationBean).addMessage(any());

		evaluationBean.remove();

		verify(session, times(2)).isLoggedIn();// zwei calls, zwei methoden
		verify(evaluationDao).delete(selectedEva);
	}

	// not logged in
	@Test
	public void testRemoveNotLoggedInFail() throws DuplicateUsernameException, DuplicateEmailException {
		when(session.isLoggedIn()).thenReturn(false);

		evaluationBean.remove();

		verify(userDao, never()).update(user);
	}

	@Test
	public void testRemoveParameterSuccess() {
		when(session.isLoggedIn()).thenReturn(true);

		evaluationBean.remove(evaluation);

		verify(evaluationDao).delete(evaluation);
	}

	// not logged in
	@Test
	public void testRemoveParameterNotLoggedInFail() {
		when(session.isLoggedIn()).thenReturn(false);

		evaluationBean.remove(evaluation);

		verify(evaluationDao, never()).delete(evaluation);
	}

}