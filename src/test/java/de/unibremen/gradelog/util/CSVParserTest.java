package de.unibremen.gradelog.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.junit.Test;

public class CSVParserTest {

	@Test(expected = IllegalArgumentException.class)
	public void testParseNoSpecifiedCellLength() {
		String result = "Jonas,Jonas,xxx,xxx,xxx,xxx,xxx,Justus,18,9a,2,xxx,19980304,justus@jonas.de";
		InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
		CSVParser parser = new CSVParser(stream, 15);
		while (parser.nextLine()) {
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseIncorrectSeparator() {
		String result = "Jonas;Jonas;xxx,xxx,xxx,xxx,xxx,Justus,18,9a,2,xxx,19980304,justus@jonas.de,xxx";
		InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
		CSVParser parser = new CSVParser(stream, 14);
		while (parser.nextLine()) {
		}
	}

	@Test
	public void testParseCorrectValue() {
		String result = "Jonas,Jonas,xxx,xxx,xxx,xxx,xxx,Justus,18,9a,2,xxx,19980304,justus@jonas.de,xxx";
		InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
		CSVParser parser = new CSVParser(stream, 14);
		String name = null;
		while (parser.nextLine()) {
			name = parser.getString(0);
		}
		assertEquals("Jonas", name);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseIncorrectDate() {
		String result = "Jonas,Jonas,xxx,xxx,xxx,xxx,xxx,Justus,18,9a,2,xxx,19980304,justus@jonas.de,xxx";
		InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
		CSVParser parser = new CSVParser(stream, 14);
		while (parser.nextLine()) {
			parser.getDate(0);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseCorrectDate() {
		String result = "Jonas,Jonas,xxx,xxx,xxx,xxx,xxx,Justus,18,9a,2,xxx,19980304,justus@jonas.de,xxx";
		InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
		CSVParser parser = new CSVParser(stream, 14);
		Date date = null;
		while (parser.nextLine()) {
			date = parser.getDate(13);
		}
		assertNotNull(date);
	}
}
