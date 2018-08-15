package de.unibremen.gradelog.persistence;

import java.sql.Date;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import de.unibremen.gradelog.model.Preference;
import org.apache.log4j.Logger;

import de.unibremen.gradelog.exception.DuplicateEmailException;
import de.unibremen.gradelog.exception.DuplicatePageNameException;
import de.unibremen.gradelog.exception.DuplicateTemplateNameException;
import de.unibremen.gradelog.exception.DuplicateUsernameException;
import de.unibremen.gradelog.exception.UnexpectedUniqueViolationException;
import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.model.Page;
import de.unibremen.gradelog.model.Template;

/**
 * Initialisiert den Datenbestand bei Start der Webapplikation.
 * 
 * @author Rune Krauss
 * @author Marco Glander
 */
@ApplicationScoped
public class DBInit {

	/**
	 * Name des Standard-Benutzers.
	 */
	private static final String DEFAULT_USER_NAME = "admin";

	/**
	 * Email-Adresse des Standard-Benutzers.
	 */
	private static final String DEFAULT_USER_EMAIL = "admin@offline.de";

	/**
	 * Passwort des Standard-Benutzers.
	 */
	private static final String DEFAULT_USER_PASSWORD = "Test123#";

	/**
	 * Der Logger für diese Klasse.
	 */
	private static final Logger logger = Logger.getLogger(DBInit.class);

	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Benutzer-Objekte übernimmt.
	 */
	@Inject
	private UserDAO userDAO;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Gruppen-Objekte übernimmt.
	 */
	@Inject
	private GroupDAO groupDAO;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Seiten-Objekte übernimmt.
	 */
	@Inject
	private PageDAO pageDAO;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Einstellungs-Objekte übernimmt.
	 */
	@Inject
	private PreferenceDAO preferenceDAO;
	/**
	 * Das Data-Access-Objekt, das die Verwaltung der Persistierung für
	 * Template-Objekte übernimmt.
	 */
	@Inject
	private TemplateDAO templateDAO;

	/**
	 * Trägt einen Standard-Benutzer in den Datenbestand ein, falls noch keine
	 * Benutzer existieren und wird beim Starten der Webanwendung ausgeführt.
	 * Zudem werden die Templates, Seiten und Gruppen erzeugt.
	 * 
	 * Der Standard-Benutzer hat den Benutzernamen {@code admin}, die
	 * Email-Adresse {@code admin@offline.de} und das Passwort {@code Test123#}.
	 * 
	 * @param context
	 *            Der Kontext der Webapplikation.
	 * @throws UnexpectedUniqueViolationException
	 *             Falls beim Erstellen des Standard-Benutzers ein Fehler
	 *             auftritt.
	 */
	public void init(@Observes @Initialized(ApplicationScoped.class) ServletContext context) {
		if (userDAO.getAllUsers().isEmpty()) {
			final User user = new User();
			user.setLogin(DEFAULT_USER_NAME);
			user.setEmail(DEFAULT_USER_EMAIL);
			user.setPassword(DEFAULT_USER_PASSWORD);

			user.setRole(User.Roles.ADMIN);
			user.getProfile().setFirstName("Admin");
			user.getProfile().setLastName("Istrator");
			user.getProfile().setPrivated(true);
			user.getProfile().setSex("male");
			user.setLanguage(Locale.GERMAN);
			user.getProfile().setBirthday(new Date(1L));

			try {
				userDAO.create(user);
			} catch (final DuplicateUsernameException ex) {
				logger.fatal(String.format(
						"Weird Error: Although there are no users, a user with the default name '%s' seems to exist.",
						DEFAULT_USER_NAME), ex);
				throw new UnexpectedUniqueViolationException(ex);
			} catch (final DuplicateEmailException ex) {
				logger.fatal(String.format(
						"Weird Error: Although there are no users, a user with the default email address '%s' seems to exist.",
						DEFAULT_USER_EMAIL), ex);
				throw new UnexpectedUniqueViolationException(ex);
			}

			try {
				userDAO.update(user);
			} catch (DuplicateUsernameException e) {
				e.printStackTrace();
			} catch (DuplicateEmailException e) {
				e.printStackTrace();
			}

			// Create pages
			final Page index = new Page();
			index.setEnglishName("Start");
			index.setGermanName("Start");
			final Page news = new Page();
			news.setEnglishName("News");
			news.setGermanName("Nachrichten");
			final Page help = new Page();
			help.setEnglishName("Help");
			help.setGermanName("Hilfe");
			final Page faq = new Page();
			faq.setEnglishName("FAQ");
			faq.setGermanName("FAQ");
			final Page imprint = new Page();
			imprint.setEnglishName("Imprint");
			imprint.setGermanName("Impressum");
			try {
				pageDAO.create(index);
				pageDAO.create(news);
				pageDAO.create(help);
				pageDAO.create(faq);
				pageDAO.create(imprint);
			} catch (DuplicatePageNameException e) {
				e.printStackTrace();
			}

			// Create preferences
			final Preference preference = Preference.getInstance();
			preference.setFileSuffix("pdf;jpg;txt;png;gif");
			preference.setFileSize(10);
			preference.setFileNumber(15);
			preferenceDAO.create(preference);

			// Templates erstellen
			Template frontend = new Template();
			frontend.setName("frontend");
			Template backend = new Template();
			backend.setName("backend");
			try {
				templateDAO.create(frontend);
				templateDAO.create(backend);
			} catch (DuplicateTemplateNameException e) {
				e.printStackTrace();
			}
		}
	}
}
