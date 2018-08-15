package de.unibremen.gradelog.controller;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unibremen.gradelog.model.Session;
import de.unibremen.gradelog.util.Assertion;

/**
 * Dieser Filter ist eine Sicherheitseinstellung im Hinblick auf den Login, d.
 * h. dass ein Zugriff auf nicht-öffentliche Seiten nur mit einem erfolgreichen
 * Login möglich ist. Wird bspw. auf '/scheduler/profile/settings.xhtml' ohne
 * einen Login zugegriffen, so wird man auf die Login-Seite weitergeleitet.
 * 
 * @author Rune Krauss
 *
 */
public class AuthorizationFilter extends AbstractController implements Filter {

	/**
	 * Die eindeutige id für Serialisierung.
	 */
	private static final long serialVersionUID = 8525625999479673090L;

	/**
	 * Erzeugt einen {@link AuthorizationFilter} mit definierter
	 * {@link Session}.
	 *
	 * @param pSession
	 * 		The {@link Session} des zu erzeugenden
	 * 		{@link AuthorizationFilter}s.
	 * @throws IllegalArgumentException
	 * 		Falls {@code pSession == null}.
     */
	@Inject
	public AuthorizationFilter(final Session pSession) {
		super(Assertion.assertNotNull(pSession));
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	/**
	 * Filtert HTTP-Requests in Bezug auf den Login, sodass ein Zugriff auf
	 * Seiten innerhalb des Zeitplaners ohne eine Authentifizierung nicht
	 * möglich ist.
	 * 
	 * @param req
	 *            Jeweiliger HTTP Request
	 * @param res
	 *            Jeweiliger HTTP Response
	 * @param chain
	 *            Filterkette, welche Filterkriterien enthält
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String pR = request.getRequestURL().toString();
		if (!pR.contains("index.xhtml") && !isLoggedIn()) {
			response.sendRedirect(request.getContextPath() + "/scheduler/index.xhtml");
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {

	}
}