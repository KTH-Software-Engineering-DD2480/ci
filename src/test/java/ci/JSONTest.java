import org.junit.jupiter.api.Test;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class JSONTest{

	// assert true if the JSON object is empty
    @Test
    void assertTrueIfEmpty() {
		// create the an empty json object
		String json = "{}";

		// define the json object
		JSONObject obj = new JSONObject(json);
        assertTrue(obj.isEmpty());
    }

	// check if a JSONObject stream is parsed correctly
	@Test
    void assertTrueIfObjectParsedCorrectly() {
		// create a json object 
		String json = "{"
		+ "  \"id\": 1, "
		+ " \"title\": \"test\", "
		+ " \"bool\" : true, "
		+ "}";

		// define the json object
		JSONObject obj = new JSONObject(json);

		// assert that we get the correct value
		assertEquals(1, obj.getInt("id"));
		assertEquals("test", obj.getString("title"));
		assertTrue(obj.getBoolean("bool"));


    }

	// check if a JSONOArray is parsed correctly
	@Test
	void assertTrueIfArrayParsedCorrectly() {
		String json = "{"
		+ "  \"items\": [ 1, 2 ] "
		+ "}";

		// define the json object
		JSONObject obj = new JSONObject(json);

		// get the json array 
		JSONArray items = obj.getJSONArray("items");

		// assert that the correct values are obtained
		assertEquals(1, items.get(0));
		assertEquals(2, items.get(1));
	
	
	}

}

