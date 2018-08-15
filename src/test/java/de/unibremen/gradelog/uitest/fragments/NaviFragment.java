package de.unibremen.gradelog.uitest.fragments;

import static org.jboss.arquillian.graphene.Graphene.guardHttp;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/*
 * @author Rune Krauss
 */
public class NaviFragment extends AbstractFragment {

	@FindBy(id = "formLogout:logout")
	private WebElement logoutButton;

	public void logout() {
		guardHttp(logoutButton).click();
	}

}
