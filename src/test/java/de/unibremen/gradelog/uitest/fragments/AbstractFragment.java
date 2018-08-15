package de.unibremen.gradelog.uitest.fragments;

import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.WebElement;

/*
 * @author Rune Krauss
 */
public abstract class AbstractFragment {

	@Root
	private WebElement root;

	public WebElement getRoot() {
		return root;
	}

	public String getID() {
		return root.getAttribute("id");
	}

	public boolean isDisplayed() {
		return root.isDisplayed();
	}

}
