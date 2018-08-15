package de.unibremen.gradelog.uitest.fragments;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/*
 * @author Rune Krauss
 */
public class TableFragment extends AbstractFragment {

	private WebElement body;

	private List<WebElement> rows;

	public int getNumberOfRows() {
		initRows();
		return rows.size();
	}

	private void initBody() {
		if (body == null) {
			body = getRoot().findElement(By.tagName("tbody"));
		}
	}

	private void initRows() {
		initBody();
		rows = body.findElements(By.tagName("tr"));
	}

	public int getNumberOfCols(final int rowIndex) {
		return getColsForRow(rowIndex).size();
	}

	public List<WebElement> getColsForRow(final int rowIndex) {
		final WebElement row = getRow(rowIndex);
		final List<WebElement> cols = row.findElements(By.tagName("td"));
		return cols;
	}

	public WebElement getRow(final int index) {
		initRows();
		return rows.size() >= index ? rows.get(0) : null;
	}

	public String getTextAt(final int rowIndex, final int colIndex) {
		final WebElement element = getElementAt(rowIndex, colIndex);
		return element == null ? null : element.getText();
	}

	public WebElement getElementAt(final int rowIndex, final int colIndex) {
		initRows();
		if (rows.size() >= rowIndex) {
			final WebElement row = rows.get(rowIndex);
			final List<WebElement> cols = row.findElements(By.tagName("td"));
			if (cols.size() >= colIndex) {
				final WebElement col = cols.get(colIndex);
				return col;
			}
		}
		return null;
	}

	public boolean rowContainsText(final int rowIndex, final String value) {
		final List<WebElement> cols = getColsForRow(rowIndex);
		for (final WebElement cell : cols) {
			if (cell.getText().equals(value)) {
				return true;
			}
		}
		return false;
	}

	public String rowToString(final int rowIndex, final String separator) {
		final List<WebElement> cols = getColsForRow(rowIndex);
		return cols.stream().map(WebElement::getText).collect(Collectors.joining(separator));
	}

}
