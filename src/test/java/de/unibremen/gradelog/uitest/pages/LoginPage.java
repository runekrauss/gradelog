package de.unibremen.gradelog.uitest.pages;

import static org.jboss.arquillian.graphene.Graphene.guardHttp;
import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import de.unibremen.gradelog.uitest.UITestUtil;

/*
 * @author Rune Krauss
 */
@Location(UITestUtil.LOGIN_URL)
public class LoginPage {

	@FindBy(id = "login:username")
	private WebElement loginUsername;

	@FindBy(id = "login:password")
	private WebElement loginPassword;

	@FindBy(id = "login:loginButton")
	private WebElement loginButton;

	@FindBy(id = "formLogout:logout")
	private WebElement logoutButton;

	public void login(final String userName, final String password) {
		loginUsername.sendKeys(userName);
		loginPassword.sendKeys(password);
		guardHttp(loginButton).click();
	}

}
