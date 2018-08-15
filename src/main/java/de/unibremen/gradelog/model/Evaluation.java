package de.unibremen.gradelog.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Klasse, die für die Selbsteinschätzungsfuntkion genutzt wird. Hält alle
 * Fragen einer Evaluation sowie deren Antworten in einem Hashtable
 *
 * @author Marco Glander
 * @author Steffen Gerken
 */
@Entity
public class Evaluation extends JPAEntity implements Comparable<Evaluation> {

    /**
     * Die eindeutige id für Serialisierung.
     */
    private static final long serialVersionUID = 805960290820822656L;

    /**
     * Zeit des Erstellens
     */
    @Column(nullable = false)
    private Timestamp date;

    /**
     * Benutzer
     */
    @ManyToOne
    private User user;

    /**
     * HashTable welche die Antworten des Benutzer speichert.
     */
    // @OneToMany
    @ElementCollection
    private Hashtable<Question, Integer> entries;

    /**
     * Liste von Fragenhalterns
     */
    @Transient
    private List<QuestionHolder> questionHolders;

    public Evaluation() {
        date = new Timestamp(0);
        user = null;
        entries = new Hashtable<>();
        updateQuestionHolders();
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Hashtable<Question, Integer> getEntries() {
        return entries;
    }

    public void setEntries(Hashtable<Question, Integer> entries) {
        this.entries = entries;
    }

    /**
     * Fügt die angegebene Antwort einer Frage zu dieser Evaluation hinzu.
     *
     * @param q die hinzuzufügende Frage
     * @param r die abgegebeitene Antwort
     * @return der Link zur nächsten Seite, die der Nutzer sehen soll
     */
    public String addEntry(Question q, int r) {
        getEntries().put(q, r);
        return null;
    }

    /**
     * Entfernt die angegebene Antwort einer Frage aus der Liste dieser
     * Evaluation
     *
     * @param q die zu löschende Antwort
     * @return der Link zur nächsten Seite, die der Nutzer sehen soll
     */
    public String removeEntry(Question q) {
        getEntries().remove(q);
        return null;
    }

    /**
     * Gibt alle vorhandenen Fragen zurück.
     *
     * @return Set aller Fragen dieser Evaluation.
     */
    public Set<Question> getQuestions() {
        return entries.keySet();
    }

    /**
     * Gibt das Resultat einer Frage zurück
     *
     * @param q die Frage, dessen Resultat abgefragt werden soll
     * @return die Antwort dieser Frage als int. Resultat bewegt sich zwischen 1
     * und 4
     */
    public int getResult(Question q) {
        if (getEntries().containsKey(q)) {
            return getEntries().get(q);
        }
        throw new IllegalArgumentException("Evaluation ID " + getId() + " does not contain a question " + q + "!");
    }

    /**
     * Aktualisiert die Fragenhalter, falls neue Fragen hinzukommen.
     */

    public void updateQuestionHolders() {
        ArrayList<QuestionHolder> result = new ArrayList<>();
        for (Question q : entries.keySet()) {
            result.add(new QuestionHolder(q, entries.get(q)));
        }
        questionHolders = result;
    }

    public void setEntries(List<QuestionHolder> list) {
        for (QuestionHolder q : list) {
            entries.put(q.getQuestion(), q.getAnswer());
        }
    }

    public List<QuestionHolder> getQuestionHolders() {
        return questionHolders;
    }

    public void setQuestionHolders(List<QuestionHolder> questionHolders) {
        this.questionHolders = questionHolders;
    }

    /**
     * Überprüft ob die angegebene ID der Objekte gleich ist.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Evaluation && ((Evaluation) other).getId() == getId();
    }

    /**
     * Lässt auf die ID und nicht den Hashcode prüfen.
     */
    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String toString() {
        return String.format("User {id: %d, user: %s}", getId(), user.getProfile().getFirstName() + " " + user.getProfile().getLastName());
    }

    @Override
    public int compareTo(Evaluation e) {
        if (e.getId() > getId())
            return 1;
        else if (e.getId() < getId())
            return -1;
        return 0;

    }

}
