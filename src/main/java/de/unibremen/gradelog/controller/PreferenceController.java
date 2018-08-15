package de.unibremen.gradelog.controller;

import de.unibremen.gradelog.exception.DuplicateTemplateNameException;
import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.model.Preference;
import de.unibremen.gradelog.persistence.PreferenceDAO;
import de.unibremen.gradelog.persistence.UserDAO;
import de.unibremen.gradelog.util.Assertion;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static de.unibremen.gradelog.util.Assertion.assertNotNull;

/**
 * Verwaltet die globalen Einstellungen wie z. B. erlaubte Dateiendungen.
 *
 * @author Rune Krauss
 */
@Named("preferenceBean")
@ViewScoped
public class PreferenceController extends AbstractController {
    /**
     * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
     * Einstellungs-Objekte übernimmt.
     */
    private final PreferenceDAO preferenceDAO;
    /**
     * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
     * Benutzer-Objekte übernimmt.
     */
    private final UserDAO userDAO;
    /**
     * Die eindeutige SerialisierungsID.
     */
    private static final long serialVersionUID = -6560012086857165997L;
    /**
     * Das Einstellungsobjekt.
     */
    private Preference preference;

    /**
     * Erzeugt einen {@link PreferenceController} mit definierter {@link Session},
     * {@link PreferenceDAO} und {@link UserDAO}.
     *
     * @param pSession
     * 		Die {@link Session} des zu erzeugenden {@link PreferenceController}s.
     * @param pPreferenceDao
     * 		Die {@link PreferenceDAO} des zu erzeugenden {@link PreferenceController}s.
     * @param pUserDao
     * 		Die {@link UserDAO} des zu erzeugenden {@link PreferenceController}s.
     * @throws IllegalArgumentException
     * 		Falls einer der übergebenen Parameter {@code null} ist.
     */
    @Inject
    public PreferenceController(final Session pSession,
                          final PreferenceDAO pPreferenceDao,
                          final UserDAO pUserDao) {
        super(assertNotNull(pSession));
        preferenceDAO = assertNotNull(pPreferenceDao);
        userDAO = assertNotNull(pUserDao);
    }

    /**
     * Initialisiert alle Einstellungen.
     */
    @PostConstruct
    public void init() {
        preference = preferenceDAO.getPreference();
    }

    /**
     * Speichert die jeweiligen Einstellungen wie z. B. die Dateiendungen ab.
     */
    public void save() {
        Logger logger = getLogger();
        if (!isLoggedIn()) {
            logger.info("Session without user tried to save the backend template!");
            return;
        }
        if (preference.getFileSuffix().length() > 128) {
            addMessage("errorMaxLength");
            return;
        }
        preference.setFileSuffix(preference.getFileSuffix().trim());
        preferenceDAO.update(assertNotNull(preference));
        addMessage("successPreferenceComplete");
    }

    /**
     * Formatiert die Dateiendungen.
     *
     * @param s
     * 		Suche
     * @param r
     * 		Ersetzung
     * @param str
     * 		Zeichenkette
     * @return
     * 		Formatierte Dateiendungen
     */
    private String rep(String s, String r, String str) {
        int start = str.indexOf(s);
        while (start != -1) {
            str = str.substring(0, start) + r + str.substring(start + s.length(), str.length());
            start = str.indexOf(s, start + r.length());
        }
        return str;
    }
    
    public boolean isLimitReached(int size)
    {
    	return preference.getFileNumber() <= size;
    }

    public void getFileExtensions() {
        addMessage("errorInvalidPDF", rep(";", ", ", preference.getFileSuffix()));
    }

    public int getFileSize() { return preference.getFileSize() * 1000000; }
    public String getTypes() {
        return "/(\\.|\\/)(" + rep(";", "|", preference.getFileSuffix())  + ")$/";
    }

    public Preference getPreference() { return preference; }
    public void setPreference(Preference preference) { this.preference = preference; }
}
