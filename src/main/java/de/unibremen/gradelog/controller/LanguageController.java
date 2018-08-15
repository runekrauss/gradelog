package de.unibremen.gradelog.controller;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.util.Assertion;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Verwaltet die Spracheinstellung, welche aktuell Englisch und Deutsch umfasst.
 * 
 * @author Rune Krauss
 */
@Named("languageBean")
@SessionScoped
public class LanguageController extends AbstractController {
	/**
	 * Die eindeutige SerialisierungsID.
	 */
	private static final long serialVersionUID = -6560012086857165997L;

	/**
	 * Aktuelle Sprache
	 */
	private Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

	/**
	 * Erzeugt einen {@link LanguageController} mit definierter
	 * {@link Session}.
	 *
	 * @param pSession
	 * 		Die {@link Session} des zu erzeugenden {@link LanguageController}s.
	 * @throws IllegalArgumentException
	 * 		Falls {@code pSession == null}.
     */
	@Inject
	public LanguageController(final Session pSession) {
		super(Assertion.assertNotNull(pSession));
	}

	@PostConstruct
	public void init() {
	}

	/**
	 * Wird ausgelöst, wenn über ein Auswahlmenü die Sprache geändert wird.
	 * Anschließend wird die Sprache im System aktualisiert.
	 * 
	 * @param language
	 *            Sprachcode
	 */
	public void onLanguageChange(final String language) {
		locale = new Locale(language);
		FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
	}

	public Locale getLocale() {
		return locale;
	}

	public String getFrontendLanguage() {
		return locale.getLanguage();
	}
}
