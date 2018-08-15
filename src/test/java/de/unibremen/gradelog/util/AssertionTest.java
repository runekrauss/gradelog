package de.unibremen.gradelog.util;

import static de.unibremen.gradelog.util.Assertion.assertNotNegative;
import static de.unibremen.gradelog.util.Assertion.assertWithoutEmpty;
import static de.unibremen.gradelog.util.Assertion.assertWithoutNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class AssertionTest {

	@Test
	public void testValidSizePositive() {
		final int input = 95;
		final int result = assertNotNegative(input);
		assertEquals(input, result);
	}

	@Test
	public void testValidSizeZero() {
		final int input = 0;
		final int result = assertNotNegative(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidSizeNegative() {
		final int input = -1;
		assertNotNegative(input);
	}

	@Test
	public void testNotNullString() {
		final String input = "a";
		final String result = assertWithoutNull(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullStringNull() {
		final String input = null;
		assertWithoutNull(input);
	}

	@Test
	public void testNotNullStringWhite() {
		final String input = " \t";
		final String result = assertWithoutNull(input);
		assertEquals(input, result);
	}

	@Test
	public void testNotNullObject() {
		final Object input = new Object();
		final Object result = assertWithoutNull(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullObjectNull() {
		final Object input = null;
		assertWithoutNull(input);
	}

	@Test
	public void testNotNullStringCollection() {
		final List<String> input = new ArrayList<>(Arrays.asList("Gunther", "Herbert"));
		final List<String> result = assertWithoutNull(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullStringCollectionNull() {
		final List<String> input = null;
		assertWithoutNull(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullStringCollectionContainsNull() {
		final List<String> input = new ArrayList<>(Arrays.asList("Gunther", null));
		assertWithoutNull(input);
	}

	@Test
	public void testNotNullFixedLengthList() {
		final List<String> input = Arrays.asList("Gunther", "Herbert");
		final List<String> result = assertWithoutNull(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullFixedLengthListContainsNull() {
		final List<String> input = Arrays.asList("Gunther", null);
		assertWithoutNull(input);
	}

	@Test
	public void testNotNullStringArray() {
		final String[] input = new String[] { "Gunther", "Herbert" };
		final String[] result = assertWithoutNull(input);
		assertArrayEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullStringArrayNull() {
		final String[] input = null;
		assertWithoutNull(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullStringArrayContainsNull() {
		final String[] input = new String[] { "Gunther", null };
		assertWithoutNull(input);
	}

	@Test
	public void testNotNullNestedCollection() {
		final List<List<String>> input = new ArrayList<>();
		input.add(new ArrayList<>(Arrays.asList("Gunther", "Herbert")));
		input.add(new ArrayList<>(Arrays.asList("Myra", "Jane", "Thomas")));
		input.add(new ArrayList<>(Arrays.asList("Lauren")));
		final List<List<String>> result = assertWithoutNull(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullNestedCollectionNull() {
		final List<List<String>> input = null;
		assertWithoutNull(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullNestedCollectionContainsNull() {
		final List<List<String>> input = new ArrayList<>();
		input.add(new ArrayList<>(Arrays.asList("Gunther", "Herbert")));
		input.add(null);
		input.add(new ArrayList<>(Arrays.asList("Lauren")));
		assertWithoutNull(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullNestedCollectionContainsNullInDepth() {
		final List<List<String>> input = new ArrayList<>();
		input.add(new ArrayList<>(Arrays.asList("Gunther", "Herbert")));
		input.add(new ArrayList<>(Arrays.asList("Myra", null, "Thomas")));
		input.add(new ArrayList<>(Arrays.asList("Lauren")));
		assertWithoutNull(input);
	}

	@Test
	public void testNotNullNestedCollectionContainsEmptyStringInDepth() {
		final List<List<String>> input = new ArrayList<>();
		input.add(new ArrayList<>(Arrays.asList("Gunther", "Herbert")));
		input.add(new ArrayList<>(Arrays.asList("Myra", "Jane", "")));
		input.add(new ArrayList<>(Arrays.asList("Lauren")));
		final List<List<String>> result = assertWithoutNull(input);
		assertEquals(input, result);
	}

	@Test
	public void testNotNullNestedArray() {
		final List<String[]> input = new ArrayList<>();
		input.add(new String[] { "Gunther", "Herbert" });
		input.add(new String[] { "Myra", "Jane", "Thomas" });
		input.add(new String[] { "Lauren" });
		final List<String[]> result = assertWithoutNull(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullNestedArrayNull() {
		final List<String[]> input = null;
		assertWithoutNull(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullNestedArrayContainsNull() {
		final List<String[]> input = new ArrayList<>();
		input.add(new String[] { "Gunther", "Herbert" });
		input.add(null);
		input.add(new String[] { "Lauren" });
		assertWithoutNull(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullNestedArrayContainsNullInDepth() {
		final List<String[]> input = new ArrayList<>();
		input.add(new String[] { "Gunther", "Herbert" });
		input.add(new String[] { "Myra", null, "Thomas" });
		input.add(new String[] { "Lauren" });
		assertWithoutNull(input);
	}

	@Test
	public void testNotNullNestedArrayContainsEmptyStringInDepth() {
		final List<String[]> input = new ArrayList<>();
		input.add(new String[] { "Gunther", "Herbert" });
		input.add(new String[] { "Myra", "Jane", "" });
		input.add(new String[] { "Lauren" });
		final List<String[]> result = assertWithoutNull(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNullDeepNestedCollectionNull() {
		final List<Set<List<Integer>>> input = null;
		assertWithoutNull(input);
	}

	@Test
	public void testNotEmptyString() {
		final String input = "a";
		final String result = assertWithoutEmpty(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyStringNull() {
		final String input = null;
		assertWithoutEmpty(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyStringWhite() {
		final String input = " \t";
		assertWithoutEmpty(input);
	}

	@Test
	public void testNotEmptyObject() {
		final Object input = new Object();
		final Object result = assertWithoutEmpty(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyObjectNull() {
		final Object input = null;
		assertWithoutEmpty(input);
	}

	@Test
	public void testNotEmptyStringCollection() {
		final List<String> input = new ArrayList<>(Arrays.asList("Gunther", "Herbert"));
		final List<String> result = assertWithoutEmpty(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyStringCollectionNull() {
		final List<String> input = null;
		assertWithoutEmpty(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyStringCollectionContainsNull() {
		final List<String> input = new ArrayList<>(Arrays.asList("Gunther", null));
		assertWithoutEmpty(input);
	}

	@Test
	public void testNotEmptyFixedLengthList() {
		final List<String> input = Arrays.asList("Gunther", "Herbert");
		final List<String> result = assertWithoutEmpty(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyFixedLengthListContainsNull() {
		final List<String> input = Arrays.asList("Gunther", null);
		assertWithoutEmpty(input);
	}

	@Test
	public void testNotEmptyStringArray() {
		final String[] input = new String[] { "Gunther", "Herbert" };
		final String[] result = assertWithoutEmpty(input);
		assertArrayEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyStringArrayNull() {
		final String[] input = null;
		assertWithoutEmpty(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyStringArrayContainsNull() {
		final String[] input = new String[] { "Gunther", null };
		assertWithoutEmpty(input);
	}

	@Test
	public void testNotEmptyNestedCollection() {
		final List<List<String>> input = new ArrayList<>();
		input.add(new ArrayList<>(Arrays.asList("Gunther", "Herbert")));
		input.add(new ArrayList<>(Arrays.asList("Myra", "Jane", "Thomas")));
		input.add(new ArrayList<>(Arrays.asList("Lauren")));
		final List<List<String>> result = assertWithoutEmpty(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyNestedCollectionNull() {
		final List<List<String>> input = null;
		assertWithoutEmpty(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyNestedCollectionContainsNull() {
		final List<List<String>> input = new ArrayList<>();
		input.add(new ArrayList<>(Arrays.asList("Gunther", "Herbert")));
		input.add(null);
		input.add(new ArrayList<>(Arrays.asList("Lauren")));
		assertWithoutEmpty(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyNestedCollectionContainsNullInDepth() {
		final List<List<String>> input = new ArrayList<>();
		input.add(new ArrayList<>(Arrays.asList("Gunther", "Herbert")));
		input.add(new ArrayList<>(Arrays.asList("Myra", null, "Thomas")));
		input.add(new ArrayList<>(Arrays.asList("Lauren")));
		assertWithoutEmpty(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyNestedCollectionContainsEmptyStringInDepth() {
		final List<List<String>> input = new ArrayList<>();
		input.add(new ArrayList<>(Arrays.asList("Gunther", "Herbert")));
		input.add(new ArrayList<>(Arrays.asList("Myra", "Jane", "")));
		input.add(new ArrayList<>(Arrays.asList("Lauren")));
		final List<List<String>> result = assertWithoutEmpty(input);
		assertEquals(input, result);
	}

	@Test
	public void testNotEmptyNestedArray() {
		final List<String[]> input = new ArrayList<>();
		input.add(new String[] { "Gunther", "Herbert" });
		input.add(new String[] { "Myra", "Jane", "Thomas" });
		input.add(new String[] { "Lauren" });
		final List<String[]> result = assertWithoutEmpty(input);
		assertEquals(input, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyNestedArrayNull() {
		final List<String[]> input = null;
		assertWithoutEmpty(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyNestedArrayContainsNull() {
		final List<String[]> input = new ArrayList<>();
		input.add(new String[] { "Gunther", "Herbert" });
		input.add(null);
		input.add(new String[] { "Lauren" });
		assertWithoutEmpty(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyNestedArrayContainsNullInDepth() {
		final List<String[]> input = new ArrayList<>();
		input.add(new String[] { "Gunther", "Herbert" });
		input.add(new String[] { "Myra", null, "Thomas" });
		input.add(new String[] { "Lauren" });
		assertWithoutEmpty(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyNestedArrayContainsEmptyStringInDepth() {
		final List<String[]> input = new ArrayList<>();
		input.add(new String[] { "Gunther", "Herbert" });
		input.add(new String[] { "Myra", "Jane", "" });
		input.add(new String[] { "Lauren" });
		assertWithoutEmpty(input);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyDeepNestedCollectionNull() {
		final List<Set<List<Integer>>> input = null;
		assertWithoutEmpty(input);
	}

}
