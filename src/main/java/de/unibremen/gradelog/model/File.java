package de.unibremen.gradelog.model;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.primefaces.model.UploadedFile;

/**
 * Klasse, die Daten zu einer hochgeladenen Datei speichert. Dazu gehören Pfad,
 * Dateiname und dem Nutzer, der diese Datei hochgeladen hat. Außerdem werden
 * wichtige Daten zur Verarbeitung hier zwischengespeichert.
 *
 * @author Marco Glander
 */
@Entity
public class File extends JPAEntity implements Comparable<File> {

    /**
     * Die eindeutige id für Serialisierung.
     */
    private static final long serialVersionUID = 3571331101667662893L;

    /**
     * Name, der auf der Seite angezeigt soll. Ist äquivalent zum Dateinamen vor
     * Generierung der UUID.
     */
    private String showName;
    /**
     * Filename
     */
    @Column(length = 256, nullable = false)
    private String name;

    /**
     * Pfad zur Datei
     */
    @Column(length = 512)
    private String path;

    /**
     * Datum
     */
    private Date date;

    /**
     * Dazugehöriger Benutzer
     */
    @ManyToOne
    private User user;

    /**
     * Dateiinhalt
     */
    private byte[] content;

    /**
     * Datei
     */
    @Transient
    private UploadedFile file;

    public File() {
        name = "";
        user = null;
        path = "";
        date = new Date(0);
    }

    public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public java.util.Date getDate() {
        return new java.util.Date(date.getTime());
    }

    public void setDate(java.util.Date date) {
        this.date = new Date(date.getTime());
    }

    public boolean fileExists() {
        return new java.io.File(path + name).exists();
    }
    
    public int compareTo(File other)
    {
    	return other.getDate().compareTo(this.getDate());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof File && ((File) other).getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String toString() {
        return String.format("File {id: %d, user: %s}", getId(), user.getProfile().getFirstName() + " " + user.getProfile().getLastName());
    }
}
