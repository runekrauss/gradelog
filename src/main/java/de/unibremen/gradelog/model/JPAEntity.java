package de.unibremen.gradelog.model;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import de.unibremen.gradelog.persistence.JPAEntityListener;

import java.io.Serializable;

/**
 * Die Basisklasse aller durch JPA persistierten Klassen.
 * 
 * @author Rune Krauss
 */
@MappedSuperclass
@EntityListeners(JPAEntityListener.class)
public class JPAEntity implements Serializable {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = -7746558258153281730L;

	/**
	 * Die innerhalb der Applikation eindeutige Id (Primärschlüssel) eines
	 * JPA-Objektes.
	 */
	@Id
	@GeneratedValue
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Setzt die Id des Objektes auf {@code 0}.
	 */
	public void clearId() {
		id = 0;
	}

	@Override
	public String toString() {
		return String.format("JPAEntity {id: %d}", id);
	}

}
