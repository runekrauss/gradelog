package de.unibremen.gradelog.integration;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

import de.unibremen.gradelog.model.User;
import de.unibremen.gradelog.util.CSVParser;
import de.unibremen.gradelog.util.LoginGenerator;

/*
 * @author Rune Krauss
 */
public class ImportTest {

	@Test
	public void testImport() {
		String result = "Jonas,Jonas,xxx,xxx,xxx,xxx,xxx,Justus,18,9a,2,xxx,19980304,justus@jonas.de,xxx";
		InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
		CSVParser csvParser = new CSVParser(stream, 14);
		User user = new User();
		while (csvParser.nextLine()) {
			user.getProfile().setFirstName(csvParser.getString(7));
			user.getProfile().setLastName(csvParser.getString(0));
			user.getProfile().setCourse(csvParser.getString(9));
			int sex = csvParser.getInt(10);
			if (sex == 1)
				user.getProfile().setSex("female");
			else if (sex == 2)
				user.getProfile().setSex("male");
			user.getProfile().setBirthday(csvParser.getDate(12));
			user.setEmail(csvParser.getString(13));
			String name = LoginGenerator.generateName(user.getProfile().getFirstName(), user.getProfile().getLastName());
			user.setLogin(name);
			String password = LoginGenerator.generatePassword(8, 16, 1, 1, 1, 1);
			user.setPassword(password);
		}
		assertEquals("Justus Jonas", user.getProfile().getFirstName() + " " + user.getProfile().getLastName());
		assertEquals(128, user.getPassword().length());
		assertTrue(7 <= user.getLogin().length() && user.getLogin().length() <= 8);
	}
}
