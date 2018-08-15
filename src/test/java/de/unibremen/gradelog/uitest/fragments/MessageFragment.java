package de.unibremen.gradelog.uitest.fragments;

/*
 * @author Rune Krauss
 */
public class MessageFragment extends AbstractFragment {

	public String getMessage() {
		return getRoot().getText();
	}

}
