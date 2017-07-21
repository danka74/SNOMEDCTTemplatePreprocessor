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

/**
 * @author danka74
 *
 */
public class TemplateData implements Iterable<Map<String, String>>{
	
	private List<Map<String, String>> dataList;

	// TDF with semicolon for multiple values
	public static TemplateData readTDFS(Reader reader) throws IOException {
		TemplateData data = new TemplateData();
		
		data.dataList = new LinkedList<Map<String, String>>();
		for(CSVRecord record : CSVFormat.TDF.withFirstRecordAsHeader().parse(reader))
			data.dataList.add(record.toMap());		
		
		return data;
	}

	@Override
	public Iterator<Map<String, String>> iterator() {
		return this.dataList.iterator();
	}
	

}
