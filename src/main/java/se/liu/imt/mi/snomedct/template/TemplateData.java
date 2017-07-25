/**
 * 
 */
package se.liu.imt.mi.snomedct.template;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author danka74
 *
 */
public class TemplateData implements Iterable<Object> {

	private JSONObject data;

	public static ValueIterator getValueIterator(JSONObject data, String key) {
		if (data.has(key))
			return new ValueIterator(data.get(key));
		else
			return new ValueIterator(null);
	}

	public static TemplateData readJSON(Reader reader) throws IOException {
		TemplateData td = new TemplateData();

		td.data = new JSONObject(new JSONTokener(reader));

		return td;

	}

	@Override
	public Iterator<Object> iterator() {
		return this.data.getJSONArray("rows").iterator();
	}

}
