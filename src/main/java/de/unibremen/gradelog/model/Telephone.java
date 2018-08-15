package de.unibremen.gradelog.model;

import javax.persistence.*;

/**
 * Dieses Model repräsentiert einen Telefoneintrag. Es enthält einen Namen und
 * eine Telefonnummer.
 *
 * @author Mirco Bockholt
 */

@Entity
public class Telephone extends JPAEntity implements Comparable<Telephone> {

    /**
     * Die eindeutige id für Serialisierung.
     */
    private static final long serialVersionUID = 2113154520629714907L;

    /**
     * Der zugehörige Benutzer
     */
    @OneToOne
    private User user;

    /**
     * Name
     */
    @Column(length = 32, nullable = false)
    private String name;

    /**
     * Telefonnummer
     */
    @Column(length = 32)
    private String number;

    /**
     * Konstruktor
     */
    public Telephone() {
        user = null;
        name = null;
        number = null;
    }

    public Telephone(User user, String name, String number) {
        this.user = user;
        this.name = name;
        this.number = number;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserId(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Vergleicht zwei Objekte der Klasse Telephone. Alternativ vergleicht es
     * die Labels innerhalb einer Telefonkette.
     *
     * @param other Das zu vergleichende Objekt
     * @return boolean Ist das übergebene Objekt mit diesem Objekt identisch?
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Telephone) {
            return other instanceof Telephone && ((Telephone) other).getId() == getId();
        } else if (other instanceof String) {
            return other.equals(this.name + "\n" + this.number);
        }
        return false;
    }

    /**
     * Methode zum vergleichen zweier Telephone Objekt Ids. Wird für die
     * Sortierung benötigt.
     *
     * @param compared Das zu vergeichende Telephone Objekt
     * @return int Ist die Id größer, kleiner, gleich groß?
     */
    @Override
    public int compareTo(final Telephone compared) {
        return this.getId() - compared.getId();
    }

    /**
     * Gibt die id zurück
     *
     * @return int Die id
     */
    @Override
    public int hashCode() {
        return getId();
    }

    /**
     * Generiert einen passenden String für das Telephone Objekt
     *
     * @return String Die String Repräsentation
     */
    @Override
    public String toString() {
        return String.format("Telephone {id: %d, user: %s}", getId(), user.getProfile().getFirstName() + " " + user.getProfile().getLastName());
    }
}
