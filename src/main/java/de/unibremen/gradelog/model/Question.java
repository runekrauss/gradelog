package de.unibremen.gradelog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Hält eine Frage, welche für Selbsteinschätzungen benutzt werden. Enthält
 * neben der ausgeschriebenen Frage nichts anderes.
 * 
 * @author Marco Glander
 * @author Steffen Gerken
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Question.findAll", query = "SELECT q FROM Question q"),
				@NamedQuery(name = "Question.findAllRemaining", query = "SELECT q FROM Question q WHERE q.inUse = true"),})
public class Question extends JPAEntity {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = -3690685211474349698L;

	/**
	 * Frage
	 */
	@Column(length = 256, nullable = false)
	private String question;
	
	/**
	 * Ist Frage noch in Gebrauch?
	 */
	private boolean inUse;

	public Question() {
		question = "";
		inUse = true;
	}

	public Question(Question source){
		this.question = source.question;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	/**
	 * Überprüft die Gleichheit der Objekte mittels der ID
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof Question && ((Question) other).getId() == getId();
	}

	/**
	 * Gibt nicht HashCode sondern die Id zurück
	 */
	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public String toString() {
		return String.format("Question {id: %d, question: %s}", getId(), question);
	}
}
