package de.unibremen.gradelog.controller;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.model.Evaluation;
import de.unibremen.gradelog.model.Question;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.persistence.EvaluationDAO;
import de.unibremen.gradelog.persistence.QuestionDAO;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.util.Assertion;

/**
 * Dieser Controller kümmert sich um die Logik hinsichtlich der Selbsteinschätzungen, d.h.
 * dem hinzufügen oder löschen von Evaluationen.
 * 
 * @author Steffen Gerken
 * @author Marco Glander
 */
@Named("evaluationBean")
@ViewScoped
public class EvaluationController extends AbstractController {
	
    /**
	 * Serialisierung-ID dieses Controllers
	 */
	private static final long serialVersionUID = 3338257271596903698L;
	
	/**
	 * Selbsteinschätzung
	 */
	private Evaluation evaluation;
	/**
	 * Selektierte Selbsteinschaetzung
	 */
	private Evaluation selectedEvaluation;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Selbsteinschätzung-Objekte übernimmt.
	 */
    private final EvaluationDAO evaluationDao;
    /**
  	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Fragen-Objekte übernimmt.
     */
    private final QuestionDAO questionDao;
    /**
  	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Benutzer-Objekte übernimmt.
     */
    private final UserDAO userDao;
    /**
     * Eine sortierte Liste für die immer gleiche Reihenfolge in einer Datatable.
     */
    private List<Evaluation> sortedEva;

	/**
	 * Erzeugt einen {@link EvaluationController} mit definierter
	 * {@link Session}, {@link EvaluationDAO}, {@link QuestionDAO} und
	 * {@link UserDAO}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden
	 * 		{@link EvaluationController}s.
	 * @param pEvaluationDao
	 * 		Die {@link EvaluationDAO} des zu erzeugenden
	 * 		{@link EvaluationController}s.
	 * @param pQuestionDao
	 * 		Die {@link QuestionDAO} des zu erzeugenden
	 * 		{@link EvaluationController}s.
     * @param pUserDao
	 * 		Die {@link UserDAO} des zu erzeugenden
	 * 		{@link EvaluationController}s.
	 * @throws IllegalArgumentException
	 * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
	@Inject
	public EvaluationController(final Session pSession,
								final EvaluationDAO pEvaluationDao,
								final QuestionDAO pQuestionDao,
								final UserDAO pUserDao) {
		super(Assertion.assertNotNull(pSession));
		evaluationDao = Assertion.assertNotNull(pEvaluationDao);
		questionDao = Assertion.assertNotNull(pQuestionDao);
		userDao = Assertion.assertNotNull(pUserDao);
	}

	/**
     * Initialisiert die verschiedenen Objekte und Listen nachdem der Konstruktor aufgerufen wurde.
     * Sortiert ein Set und fügt es in eine Liste ein und aktualisiert danach die Fragenhalter.
     */
    @PostConstruct
	public void init() {
		evaluation = new Evaluation();
		sortedEva = new ArrayList<>();
		sortedEva.addAll(getSession().getUser().getEvaluations());
		Collections.sort(sortedEva);
		for(Question q : questionDao.getAllRemainingQuestions())
		{
			evaluation.getEntries().put(q, 0);
		}
		evaluation.updateQuestionHolders();
	}

	/**
	 * Setzt die richtigen Attributwerde und speichert danach die Selbsteinschätzung in der Datenbank.
	 * Der angemeldete Benutzer wird zusätzlich in der Datenbank aktualisiert.
	 * 
	 */
    public void save() {
    	Logger logger = getLogger();
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save calendar values!");
			return;
		}
		User user = getSession().getUser();
		assertNotNull(user);
		user.addEvaluation(assertNotNull(evaluation));
		evaluation.setUser(user);
		evaluation.setDate(new Timestamp(new Date().getTime()));
		evaluation.setEntries(evaluation.getQuestionHolders());
		evaluationDao.create(evaluation);
		addMessage("evaluationSaved");
		try {
			userDao.update(user);
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging("CustomPageController: Username ", e, logger, Level.DEBUG,
					"errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging("CustomPageController: Email ", e, logger, Level.DEBUG, "errorEmailAlreadyInUse",
					user.getEmail());
		}
		
		init();
	}
    /**
     * Aktualisiert eine Selbsteineinschätzung in der Datenbank.
     * 
     * @param e Die zu aktualisiernde Selbsteinschätzung.
     */
	public void save(Evaluation e) {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to save evaluation values!");
			return;
		}
		evaluationDao.update(e);
	}

	/**
	 * Löscht den Datensatz dieses Models aus der Datenbank. Aktualisiert außerdem den angemeldeten Benutzer.
	 * 
	 */
	public void remove() {
		Logger logger = getLogger();
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to delete calendar values!");
			return;
		}
		User user = getSession().getUser();
		user.removeEvaluation(getSelectedEvaluation());
		try {
			userDao.update(user);
		} catch (final DuplicateUsernameException e) {
			addMessageWithLogging("CustomPageController: Username ", e, logger, Level.DEBUG,
					"errorUsernameAlreadyInUse", user.getLogin());
		} catch (final DuplicateEmailException e) {
			addMessageWithLogging("CustomPageController: Email ", e, logger, Level.DEBUG, "errorEmailAlreadyInUse",
					user.getEmail());
		}
		remove(getSelectedEvaluation());
		addMessage("evaluationDeleted");
	}
	
	/**
	 * Löscht eine Selbsteinschätzung aus er Datenbank.
	 * 
	 * @param e Die zu löschende Selbsteinschätzung.
	 */
	public void remove(Evaluation e) {
		if (!isLoggedIn()) {
			getLogger().info("Session without user tried to delete calendar values!");
			return;
		}
		evaluationDao.delete(e);
	}
	
	public Set<Evaluation> getEvaluations() {
		return getSession().getUser().getEvaluations();
	}
	
	public Evaluation getEvaluation()
	{
		return evaluation;
	}
	
	public Evaluation getSelectedEvaluation() {
		return selectedEvaluation;
	}
	
	public void setSelectedEvaluation(Evaluation e){
		selectedEvaluation = e;
	}
	
	public List<Evaluation> getSortedEvas(){
		return sortedEva;
	}
	
	public int getListLength(){
		return sortedEva.size();
	}
}
