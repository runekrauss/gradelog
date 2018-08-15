package de.unibremen.gradelog.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

/**
 * Beinhaltet die jeweilige globale Einstellung.
 *
 * @author Rune Krauss
 */
@Entity
@NamedQueries({ @NamedQuery(name = "Preference.find", query = "SELECT p FROM Preference p") })
public class Preference extends JPAEntity {

    /**
     * Die eindeutige id für Serialisierung.
     */
    private static final long serialVersionUID = 5319096729830236396L;

    /**
     * Singleton-Variable
     */
    @Transient
    private static Preference instance;

    /**
     * Erlaubte Dateiendungen
     */
    @Column(length = 128, nullable = false, unique = true)
    private String fileSuffix;

    /**
     * Erlaubte Dateiendungen
     */
    @Column(nullable = false, unique = true)
    private int fileSize;

    /**
     * Erlaubte Dateianzahl
     */
    @Column(nullable = false, unique = true)
    private int fileNumber;

    /**
     * Initialisiert ein Objekt dieser Klasse mit einem leeren Inhalt.
     */
    public Preference() {}

    public static Preference getInstance () {
        if (Preference.instance == null) {
            Preference.instance = new Preference();
        }
        return Preference.instance;
    }

    public String getFileSuffix() { return fileSuffix; }
    public void setFileSuffix(String fileSuffix) { this.fileSuffix = fileSuffix; }
    public int getFileSize() { return fileSize; }
    public void setFileSize(int fileSize) { this.fileSize = fileSize; }
    public int getFileNumber() { return fileNumber; }
    public void setFileNumber(int fileNumber) { this.fileNumber = fileNumber; }
}
