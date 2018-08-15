package de.unibremen.gradelog.model;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;

/**
 * Diese Klasse repräsentiert das Profil des Nutzers dieser Applikation.
 *
 * @author Rune Krauss
 */
@Entity
public class Profile extends JPAEntity {
    /**
     * Vorname
     */
    @Column(length = 64, nullable = false)
    private String firstName;

    /**
     * Nachname
     */
    @Column(length = 64, nullable = false)
    private String lastName;

    /**
     * Profilbild
     */
    @Column(length = 102400)
    private byte[] picture;

    /**
     * Status Profilbild
     */
    private boolean ownPicture;

    /**
     * Text des Benutzers
     */
    @Column(length = 4096)
    private String aboutMe;

    /**
     * Geburtsdatum
     */
    private Date birthday;

    /**
     * Status des Profils
     */
    private boolean privated;

    /**
     * Geschlecht
     */
    @Column(length = 8)
    private String sex;

    /**
     * Kurs bzw. Klasse
     */
    private String course;

    @OneToOne(targetEntity = User.class)
    private User user;

    Profile() {
        firstName = "";
        lastName = "";
        aboutMe = "";
        sex = "male";
        birthday = new Date(0);
        privated = false;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    /**
     * Liefert das Bild dieses Users aus der Profilseite. Gibt es kein eigenes
     * Bild, so wird ein Standardbild ausgewählt, je nachdem, ob man männlich
     * oder weiblich ist.
     *
     * @return Bild des Users
     */
    public StreamedContent getPicture() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                return new DefaultStreamedContent(new ByteArrayInputStream(picture), "image/jpg");
            } catch (Exception e) {
                BufferedImage img = ImageIO.read(
                        context.getExternalContext().getResourceAsStream("/resources/img/profile/" + sex + ".jpg"));
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

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(final String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public java.util.Date getBirthday() {
        return new java.util.Date(birthday.getTime());
    }

    public void setBirthday(final java.util.Date birthday) {
        this.birthday = new java.sql.Date(birthday.getTime());
    }

    public boolean isPrivated() {
        return privated;
    }

    public void setPrivated(final boolean privated) {
        this.privated = privated;
    }

    public String getPureName() {
        return firstName + " " + lastName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(final String sex) {
        this.sex = sex;
    }

    public boolean isOwnPicture() { return ownPicture; }

    public void setOwnPicture(final boolean ownPicture) {
        this.ownPicture = ownPicture;
    }

    public String getCourse() { return course; }

    public void setCourse(final String course) { this.course = course; }
}
