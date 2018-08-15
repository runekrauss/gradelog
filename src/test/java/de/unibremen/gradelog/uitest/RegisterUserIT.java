package de.unibremen.gradelog.uitest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import de.unibremen.gradelog.uitest.fragments.TableFragment;
import de.unibremen.gradelog.uitest.fragments.NaviFragment;
import de.unibremen.gradelog.uitest.pages.LoginPage;
import de.unibremen.gradelog.uitest.pages.UserPage;

/*
 * @author Rune Krauss
 */
@RunWith(Arquillian.class)
public class RegisterUserIT {

	private static final String WEBAPP_SRC = "src/main/webapp/";

	private static final String DEFAULT_USER_NAME = "admin";

	private static final String DEFAULT_USER_PASSWORD = "Test123#";

	private static final String DEFAULT_USER_EMAIL = "admin@offline.de";

	@Deployment(testable = false)
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class, "gradelog.war").addPackages(true, "de.unibremen.gradelog")
				.addAsWebResource(new File(WEBAPP_SRC + "scheduler/admin/", "users.xhtml"),
						"scheduler/admin/users.xhtml")
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsWebResource(new File(WEBAPP_SRC, "scheduler/index.xhtml"), "scheduler/index.xhtml")
				.addAsWebResource(new File(WEBAPP_SRC, "scheduler/admin/users.xhtml"), "scheduler/admin/users.xhtml")
				.addAsWebResource(new File(WEBAPP_SRC, "errorPages/not_found.xhtml"), "errorPages/not_found.xhtml")
				.addAsWebResource(new File(WEBAPP_SRC, "resources/css/bootstrap.min.css"),
						"resources/css/bootstrap.min.css")
				.addAsWebResource(new File(WEBAPP_SRC, "resources/css/scheduler.css"), "resources/css/scheduler.css")
				.addAsWebResource(new File(WEBAPP_SRC, "resources/css/error.css"), "resources/css/error.css")
				.addAsWebResource(new File(WEBAPP_SRC, "resources/js/bootstrap.min.js"),
						"resources/js/bootstrap.min.js")
				.addAsWebResource(new File(WEBAPP_SRC, "resources/js/jquery-3.1.1.min.js"),
						"resources/js/jquery-3.1.1.min.js")
				.addAsWebResource(new File(WEBAPP_SRC, "resources/img/icons/csv.png"), "resources/img/icons/csv.png")
				.addAsWebResource(new File(WEBAPP_SRC, "resources/img/icons/pdf.png"), "resources/img/icons/pdf.png")
				.addAsWebResource(new File(WEBAPP_SRC, "resources/img/icons/xls.png"), "resources/img/icons/xls.png")
				.addAsWebResource(new File(WEBAPP_SRC, "resources/img/icons/xml.png"), "resources/img/icons/xml.png")
				.addAsWebResource(new File(WEBAPP_SRC, "templates/scheduler/layout.xhtml"),
						"templates/scheduler/layout.xhtml")
				.addAsWebResource(new File(WEBAPP_SRC, "templates/scheduler/content.xhtml"),
						"templates/scheduler/content.xhtml")
				.addAsWebResource(new File(WEBAPP_SRC, "templates/scheduler/header.xhtml"),
						"templates/scheduler/header.xhtml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsWebInfResource(new File(WEBAPP_SRC, "WEB-INF/faces-config.xml"))
				.setWebXML(new File("src/test/resources/web.xml"));
	}

	@Page
	private UserPage testPage;

	@Page
	private LoginPage indexPage;

	@Drone
	WebDriver browser;

	@ArquillianResource
	URL deploymentURL;

	@Before
	public void loginAndLoadTestPage() {
		indexPage = Graphene.goTo(LoginPage.class);
		indexPage.login(DEFAULT_USER_NAME, DEFAULT_USER_PASSWORD);
		testPage = Graphene.goTo(UserPage.class);
	}

	@After
	public void logout() {
		NaviFragment topMenu = testPage.getNavi();
		topMenu.logout();
	}

	@Test
	@InSequence(1)
	public void testAllFieldsEmpty() {
		testPage.create();
		final TableFragment userTable = testPage.getUserTable();
		final int rows = userTable.getNumberOfRows();
		assertThat(rows, is(2));
	}

	@Test
	@InSequence(2)
	public void testUsernameFieldEmpty() {
		testPage.setEmail("nobody@online.com");
		testPage.setPassword("Test123!");
		testPage.setPassword2("Test123!");
		testPage.setFirstName("Max");
		testPage.setSex("male");
		testPage.setSurname("Mustermann");
		testPage.setRole("student");
		testPage.create();
		final TableFragment userTable = testPage.getUserTable();
		final int rows = userTable.getNumberOfRows();
		assertThat(rows, is(2));
	}

	@Test
	@InSequence(3)
	public void testPasswordFieldEmpty() {
		testPage.setUsername("max");
		testPage.setEmail("nobody@online.com");
		testPage.setFirstName("Max");
		testPage.setSurname("Mustermann");
		testPage.setSex("male");
		testPage.setRole("student");
		testPage.create();
		final TableFragment userTable = testPage.getUserTable();
		final int rows = userTable.getNumberOfRows();
		assertThat(rows, is(2));
	}

	@Test
	@InSequence(4)
	public void testEmailFieldEmpty() {
		testPage.setUsername("max");
		testPage.setPassword("Test123!");
		testPage.setPassword2("Test123!");
		testPage.setFirstName("Max");
		testPage.setSex("male");
		testPage.setSurname("Mustermann");
		testPage.setRole("student");
		testPage.create();
		final TableFragment userTable = testPage.getUserTable();
		final int rows = userTable.getNumberOfRows();
		assertThat(rows, is(2));
	}

	@Test
	@InSequence(5)
	public void testUsernameAndPasswordFieldEmpty() {
		testPage.setEmail("nobody@online.com");
		testPage.setFirstName("Max");
		testPage.setSurname("Mustermann");
		testPage.setSex("male");
		testPage.setRole("student");
		testPage.create();
		final TableFragment userTable = testPage.getUserTable();
		final int rows = userTable.getNumberOfRows();
		assertThat(rows, is(2));
	}

	@Test
	@InSequence(6)
	public void testUsernameAndEmailFieldEmpty() {
		testPage.setPassword("Test123!");
		testPage.setPassword2("Test123!");
		testPage.setFirstName("Max");
		testPage.setSurname("Mustermann");
		testPage.setSex("male");
		testPage.setRole("student");
		testPage.create();
		final TableFragment userTable = testPage.getUserTable();
		final int rows = userTable.getNumberOfRows();
		assertThat(rows, is(2));
	}

	@Test
	@InSequence(7)
	public void testPasswordAndEmailFieldEmpty() {
		testPage.setUsername("max");
		testPage.setFirstName("Max");
		testPage.setSurname("Mustermann");
		testPage.setSex("male");
		testPage.setRole("student");
		testPage.create();
		final TableFragment userTable = testPage.getUserTable();
		final int rows = userTable.getNumberOfRows();
		assertThat(rows, is(2));
	}

	@Test
	@InSequence(8)
	public void testExistingUsername() {
		testPage.setUsername(DEFAULT_USER_NAME);
		testPage.setEmail("nobody@online.com");
		testPage.setPassword("Test123!");
		testPage.setPassword2("Test123!");
		testPage.setFirstName("Max");
		testPage.setSex("male");
		testPage.setSurname("Mustermann");
		testPage.setRole("student");
		testPage.create();
		final TableFragment userTable = testPage.getUserTable();
		final int rows = userTable.getNumberOfRows();
		assertThat(rows, is(2));
	}

	@Test
	@InSequence(9)
	public void testExistingEmail() {
		testPage.setUsername("max");
		testPage.setEmail(DEFAULT_USER_EMAIL);
		testPage.setPassword("Test123!");
		testPage.setPassword2("Test123!");
		testPage.setFirstName("Max");
		testPage.setSex("male");
		testPage.setSurname("Mustermann");
		testPage.setRole("student");
		testPage.create();
		final TableFragment userTable = testPage.getUserTable();
		final int rows = userTable.getNumberOfRows();
		assertThat(rows, is(2));
	}

	@Test
	@InSequence(10)
	public void testWrongPassword() {
		testPage.setUsername("max");
		testPage.setEmail("nobody@online.com");
		testPage.setPassword("Test123!");
		testPage.setPassword2("Test123#");
		testPage.setFirstName("Max");
		testPage.setSex("male");
		testPage.setSurname("Mustermann");
		testPage.setRole("student");
		testPage.create();
		final TableFragment userTable = testPage.getUserTable();
		final int rows = userTable.getNumberOfRows();
		assertThat(rows, is(2));
	}

	@Test
	@InSequence(11)
	public void testRegisterUserAndLogin() {
		testPage.setUsername("max");
		testPage.setEmail("nobody@online.com");
		testPage.setPassword("Test123!");
		testPage.setPassword2("Test123!");
		testPage.setFirstName("Max");
		testPage.setSex("male");
		testPage.setSurname("Mustermann");
		final TableFragment userTable = testPage.getUserTable();
		final int rows = userTable.getNumberOfRows();
		assertThat(rows, is(2));

		testPage.create();

		logout();

		indexPage = Graphene.goTo(LoginPage.class);
		indexPage.login("max", "Test123!");

		try {
			browser.findElement(By.id("formLogout:logout"));
		} catch (final Exception e) {
			fail("Login was not successful after registration: " + e.getMessage());
		}

	}

}
