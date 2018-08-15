package de.unibremen.gradelog.model;

import java.io.Serializable;

/**
 * Hält die Frage und die Antwort auf diese. Wird benötigt, um Daten für den
 * User zu speichern für das Modul Selbsteinschätzung.
 * 
 * @author Marco Glander
 * @author Steffen Gerken
 */
public class QuestionHolder implements Serializable {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = -4550755551474175310L;

	/**
	 * Frage
	 */
	private Question question;

	/**
	 * Antwort
	 */
	private int answer;

	public QuestionHolder(Question question, int answer) {
		this.question = question;
		this.answer = answer;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

}
