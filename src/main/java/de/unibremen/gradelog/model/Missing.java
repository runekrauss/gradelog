package de.unibremen.gradelog.model;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;
import javax.persistence.*;

/**
 * Hält die Daten einer Abwesenheitsmeldung eines Nutzers. Dazu gehören Start-
 * und Endzeit der Abwesenheit, Beschreibung, Bestätigung und ein optionales
 * Bestätigungsbild. Enthält weiterhin Daten, die bei der Erstellung nützlich
 * sind, welche nicht persistiert werden.
 *
 * @author Marco Glander
 * @author Mirco Bockholt
 */
@Entity
public class Missing extends JPAEntity {

    /**
     * Die eindeutige id für Serialisierung.
     */
    private static final long serialVersionUID = 9079148591360064426L;

    /**
     * Startzeit
     */
    @Column(nullable = false)
    private Timestamp startTime;

    /**
     * Endzeit
     */
    @Column(nullable = false)
    private Timestamp endTime;

    /**
     * Beschreibung
     */
    @Column(length = 1024)
    private String description;

    /**
     * Bestätigung
     */
    private boolean confirmed;

    /**
     * Bild (Bytestream)
     */
    @Column(length = 10240000)
    private byte[] image;

    /**
     * Zugehöriger Benutzer
     */
    @ManyToOne
    private User user;

    /**
     * Attribut, in welchem das hochgeladene Bild hinterlegt wird
     */
    @Transient
    private UploadedFile file;

    /**
     * Dieses Model repräsentiert eine Fehlzeit
     */
    public Missing() {
        startTime = new Timestamp(new Date().getTime());
        endTime = new Timestamp(new Date().getTime());
        description = "";
        confirmed = false;
        image = null;
        user = null;
    }

    public Date getStartTime() {
        return new Date(startTime.getTime());
    }

    public void setStartTime(Date startTime) {
        this.startTime = new Timestamp(startTime.getTime());
    }

    public String getStartTimeString() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(new Date(startTime.getTime()));
    }

    public long getStartTimeTimestamp() {
        return startTime.getTime();
    }

    public Date getEndTime() {
        return new Date(endTime.getTime());
    }

    public void setEndTime(Date endTime) {
        this.endTime = new Timestamp(endTime.getTime());
    }

    public String getEndTimeString() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(new Date(endTime.getTime()));
    }

    public long getEndTimeTimestamp() {
        return endTime.getTime();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public StreamedContent getImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                return new DefaultStreamedContent(new ByteArrayInputStream(image), "image/jpg");
            } catch (Exception e) {
                BufferedImage img = ImageIO.read(context.getExternalContext()
                        .getResourceAsStream("/resources/img/placeholder/placeholder.jpeg"));
                int w = img.getWidth(null);
                int h = img.getHeight(null);
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.getGraphics();
                g.drawImage(img, 0, 0, w, h, null);
                ImageIO.write(bi, "jpg", bos);
                return new DefaultStreamedContent(new ByteArrayInputStream(bos.toByteArray()), "image/jpg");
            }
        }
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public User getUser() {
        return user;
    }

    public void setUserId(User user) {
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Missing && ((Missing) other).getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String toString() {
        return String.format("Missing {id: %d, user: %s, image: %s}", getId(),
                user.getProfile().getFirstName() + " " + user.getProfile().getLastName(), image);
    }
}
