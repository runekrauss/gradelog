package de.unibremen.gradelog.controller;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.unibremen.gradelog.model.Question;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.persistence.QuestionDAO;
import de.unibremen.gradelog.util.Assertion;

/**
 * Dieser Controller kümmert sich um die Logik hinsichtlich der Fragen, d.h dem
 * Hinzufügen oder Löschen usw. einer Frage.
 * 
 * @author Steffen Gerken
 *
 */
@Named("questionBean")
@ViewScoped
public class QuestionController extends AbstractController {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 8258572736595937017L;
	/**
	 * Frage
	 */
	private Question question;
	/**
	 * Selektierte Frage
	 */
	private Question selectedQuestion;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Fragen-Objekte übernimmt.
	 */
	private final QuestionDAO questionDao;

	/**
	 * Erzeugt einen {@link QuestionController} mit definierter {@link Session}
	 * und {@link QuestionDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link QuestionController}s.
	 * @param pQuestionDao
	 * 		Die {@link QuestionDAO} des zu erzeugenden
	 * 		{@link QuestionController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public QuestionController(final Session pSession,
							  final QuestionDAO pQuestionDao) {
		super(Assertion.assertNotNull(pSession));
		questionDao = Assertion.assertNotNull(pQuestionDao);
	}

	/**
	 * Wird nach dem Konstruktor aufgerufen und initialisiert eine neue Frage.
	 */
	@PostConstruct
	public void init() {
		question = new Question();
		selectedQuestion = new Question();
	}

	/**
	 * Schreibt die Attributswerte dieses Models per DAO in die Datenbank
	 * 
	 */
	public void save() {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save calendar values!");
			return;
		}
		questionDao.create(question);
		addMessage("questionCreated");
		init();
	}

	/**
	 * Aktualisiert die übergebene Frage in der Datenbank.
	 * 
	 * @param q
	 *            , die zu aktualisierende Frage
	 */
	public void save(Question q) {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save question values!");
			return;
		}
		if (q != null) {
			questionDao.update(q);
			addMessage("questionCreated");
		}
	}

	/**
	 * Löscht den Datensatz dieses Models aus der Datenbank s
	 */
	public void remove() {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to delete question values!");
			return;
		}
		remove(getSelectedQuestion());
	}

	/**
	 * Löscht eine Frage aus der Datenbank.
	 * 
	 * @param q
	 *            Die zu löschende Frage
	 */
	public void remove(Question q) {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to delete question values!");
			return;
		}
		if (q != null) {
			q.setInUse(false);
			questionDao.update(q);
			addMessage("questionDeleted");
		}

	}

	public Question getQuestion() {
		return question;
	}

	public Question getSelectedQuestion() {
		return selectedQuestion;
	}

	public void setSelectedQuestion(Question selectedQuestion) {
		this.selectedQuestion = selectedQuestion;
	}

}
