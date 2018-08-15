package de.unibremen.gradelog.uitest.pages;

import static org.jboss.arquillian.graphene.Graphene.guardAjax;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import de.unibremen.gradelog.uitest.UITestUtil;
import de.unibremen.gradelog.uitest.fragments.MessageFragment;
import de.unibremen.gradelog.uitest.fragments.TableFragment;
import de.unibremen.gradelog.uitest.fragments.NaviFragment;

/*
 * @author Rune Krauss
 */
@Location(UITestUtil.USER_URL)
public class UserPage {

	@FindBy(id = "createUserForm:login")
	private WebElement userName;

	@FindBy(id = "createUserForm:growl")
	private MessageFragment userNameMessage;

	@FindBy(id = "createUserForm:password")
	private WebElement password;

	@FindBy(id = "createUserForm:password2")
	private WebElement password2;

	@FindBy(id = "createUserForm:email")
	private WebElement email;

	@FindBy(id = "createUserForm:sex")
	private WebElement sex;

	@FindBy(id = "createUserForm:name")
	private WebElement firstName;

	@FindBy(id = "createUserForm:surname")
	private WebElement surname;

	@FindBy(id = "createUserForm:role")
	private WebElement role;

	@FindBy(id = "createUserForm:create")
	private WebElement createButton;

	@FindBy(id = "userEdit:users")
	private TableFragment userTable;

	@FindBy(id = "formLogout")
	private NaviFragment navi;

	public String getUsername() {
		return userName.getText();
	}

	public void setUsername(final String input) {
		userName.sendKeys(input);
	}

	public String getFirstName() {
		return firstName.getText();
	}

	public void setFirstName(final String input) {
		firstName.sendKeys(input);
	}

	public String getSurname() {
		return surname.getText();
	}

	public void setSurname(final String input) {
		surname.sendKeys(input);
	}

	public String getRole() {
		return role.getText();
	}

	public void setRole(final String input) {
		role.sendKeys(input);
	}

	public String getPassword() {
		return password.getTagName();
	}

	public void setPassword(final String input) {
		password.sendKeys(input);
	}

	public String getPassword2() {
		return password2.getTagName();
	}

	public void setPassword2(final String input) {
		password2.sendKeys(input);
	}

	public String getEmail() {
		return email.getText();
	}

	public void setEmail(final String input) {
		email.sendKeys(input);
	}

	public String getSex() {
		return sex.getText();
	}

	public void setSex(final String input) {
		sex.sendKeys(input);
	}

	public TableFragment getUserTable() {
		return userTable;
	}

	public void create() {
		guardAjax(createButton).click();
	}

	public NaviFragment getNavi() {
		return navi;
	}

}
