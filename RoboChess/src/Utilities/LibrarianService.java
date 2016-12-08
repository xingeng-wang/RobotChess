/*
 * University of Saskatchewan 
 * CMPT 370 - Intermediate Software Engineering
 * Team B5:
 *     Lin, Yuchen
 * 	   Nelson, Jordan
 * 	   Park, Ryan
 * 	   Wang, Xingeng
 * 	   Van Heerde, Willie
 * 
 * Class: LibrarianService
 */
package Utilities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Handles the 'LIST', 'REGISTER', 'DELETE', 'DOWNLOAD', and 'UPDATE' requests
 * to the robot librarian.
 * 
 * @version 0.01
 * @author Ryan Park
 */
public class LibrarianService {
	
	/** Address of the webserver hosting the robot librarian. */
	private static final String WEBSERVER_ADDRESS = "http://gpu0.usask.ca:20001";
	
	/** Port number of the webserver hosting the robot librarian. */
	private static final int WEBSERVER_PORT = 20001;
	
	/** A JSON parser. */
	private static final JSONParser JSON_PARSER = new JSONParser();

	/**
	 * Returns a json formatted list of robots. Results are filtered
	 * based on the fields specified by requestFields
	 * @param requestFields fields used to filter the requests
	 * @return a list of JSONObjects
	 */
	public JSONObject list(BiMap<String, ?> requestFields) throws Exception {
		/* Construct the json body as a string. */
		String jsonBody = "{ \"list-request\" : { ";
		
		if (requestFields.containsKey("team")) {
			jsonBody += "\"team\" : \"" + requestFields.get("team") + "\", ";
		}
		
		if (requestFields.containsKey("class")) {
			jsonBody += "\"class\" : \"" + requestFields.get("class") + "\", ";
		}
		
		if (requestFields.containsKey("name")) {
			jsonBody += "\"name\" : \"" + requestFields.get("name") + "\", ";
		}
		
		if (requestFields.containsKey("wins")) {
			jsonBody += "\"wins\" : [" + requestFields.get("wins").toString() + "], ";
		}
		
		if (requestFields.containsKey("losses")) {
			jsonBody += "\"losses\" : [" + requestFields.get("losses").toString() + "], ";
		}
		
		if (requestFields.containsKey("matches")) {
			jsonBody += "\"matches\" : [" + requestFields.get("matches").toString() + "], ";
		}
		
		jsonBody += "\"data\" : \"" + requestFields.get("data") + "\" } }";
		
		System.out.println(jsonBody);
		
		return sendAndReceive(jsonBody);
	}
	
	/**
	 * Registers a new robot.
	 * @param requestFields
	 * @return json formatted object indicating success or failure
	 */
	public JSONObject register(BiMap<String, String> requestFields) throws Exception {
		/* Check parameters */
		if (!requestFields.containsKey("team") || 
				!requestFields.containsKey("class") || 
				!requestFields.containsKey("name") ||
				!requestFields.containsKey("code")) {
			throw new Exception("Missing required fields.");
		}
		
		/* Check if team and name combination is unique. */
		BiMap<String, String> request = HashBiMap.create();
		request.put("team", requestFields.get("team"));
		request.put("name", requestFields.get("name"));
		request.put("data", "full");
		
		JSONObject obj = this.list(request);
		JSONArray jsonArr = (JSONArray) obj.get("script");
		if (jsonArr.size() > 0) {
			throw new Exception("Team and Name combination is not unique.");
		}
		
		/* Construct the json body as a string. */
		String jsonBody = "{ \"register\" : { \"script\" : { ";
		jsonBody += "\"team\" : \"" + requestFields.get("team") + "\", ";
		jsonBody += "\"class\" : \"" + requestFields.get("class") + "\", ";
		jsonBody += "\"name\" : \"" + requestFields.get("name") + "\", ";
		jsonBody += "\"code\" : [" + requestFields.get("code").toString() + "] } } ";
		
		System.out.println(jsonBody);
		
		return sendAndReceive(jsonBody);
	}
	
	/**
	 * Deletes the robot script specified by requestFields.
	 * @param requestFields
	 * @return json formatted object indicating success or failure
	 */
	public JSONObject delete(BiMap<String, String> requestFields) throws Exception{
		/* Check parameters */
		if (!requestFields.containsKey("team") || !requestFields.containsKey("name")) {
			throw new Exception("Missing required fields.");
		}
		
		/* Check if team and name combination exists and is unique. */
		BiMap<String, String> request = HashBiMap.create();
		request.put("team", requestFields.get("team"));
		request.put("name", requestFields.get("name"));
		request.put("data", "full");
		
		JSONObject obj = this.list(request);
		JSONArray jsonArr = (JSONArray) obj.get("script");
		if (jsonArr.size() == 0) {
			throw new Exception("Team and Name combination does not exist.");
		} else if (jsonArr.size() > 1) {
			throw new Exception("Team and Name combination is not unique.");
		}
		
		/* Construct the json body as a string. */
		String jsonBody = "{ \"delete\" : { ";
		jsonBody += "\"team\" : \"" + requestFields.get("team") + "\", ";
		jsonBody += "\"name\" : \"" + requestFields.get("name") + "\" } } ";
		
		System.out.println(jsonBody);
		
		return sendAndReceive(jsonBody);
	}
	
	/**
	 * Same as list but yields a single json formatted record.
	 * @param requestFields
	 * @return a single JSONObject
	 */
	public JSONObject download(BiMap<String, String> requestFields) throws Exception {
		/* Do 'list' and make sure there is one script. */
		JSONObject jsonObj = this.list(requestFields);
		JSONArray jsonArr = (JSONArray) jsonObj.get("script");
		if (jsonArr.size() == 0) {
			throw new Exception("No matching results found.");
		} else if (jsonArr.size() > 1) {
			throw new Exception("More than one match was found.");
		}
		
		return jsonObj;
	}
	
	/**
	 * Records match data.
	 * @param requestFields the match data
	 * @return json formatted object indicating success or failure
	 */
	public JSONObject update(BiMap<String, String> requestFields) throws Exception {
		/* Check parameters */
		if (!requestFields.containsKey("team") || 
				!requestFields.containsKey("name") ||
				!requestFields.containsKey("wins") ||
				!requestFields.containsKey("losses") ||
				!requestFields.containsKey("lived") ||
				!requestFields.containsKey("died") ||
				!requestFields.containsKey("absorbed") ||
				!requestFields.containsKey("killed") ||
				!requestFields.containsKey("distance")) {
			throw new Exception("Missing required fields.");
		}
		
		/* Check if team and name combination is unique. */
		BiMap<String, String> request = HashBiMap.create();
		request.put("team", requestFields.get("team"));
		request.put("name", requestFields.get("name"));
		request.put("data", "full");
		
		JSONObject obj = this.list(request);
		JSONArray jsonArr = (JSONArray) obj.get("script");
		if (jsonArr.size() == 0) {
			throw new Exception("Team and Name combination does not exist.");
		} else if (jsonArr.size() > 1) {
			throw new Exception("Team and Name combination is not unique.");
		}
		
		/* Construct the json body as a string. */
		String jsonBody = "{ \"update\" : { ";
		jsonBody += "\"team\" : \"" + requestFields.get("team") + "\", ";
		jsonBody += "\"name\" : \"" + requestFields.get("name") + "\", ";
		jsonBody += "\"wins\" : \"" + requestFields.get("wins") + "\", ";
		jsonBody += "\"losses\" : \"" + requestFields.get("losses") + "\", ";
		jsonBody += "\"lived\" : \"" + requestFields.get("lived") + "\", ";
		jsonBody += "\"died\" : \"" + requestFields.get("died") + "\", ";
		jsonBody += "\"absorbed\" : \"" + requestFields.get("absorbed") + "\", ";
		jsonBody += "\"killed\" : \"" + requestFields.get("killed") + "\", ";
		jsonBody += "\"distance\" : \"" + requestFields.get("distance") + "\" } } ";
		
		System.out.println(jsonBody);
		
		return sendAndReceive(jsonBody);
	}
	
	/**
	 * Sends the jsonBody over sockets and returns the reply.
	 * @param jsonBody
	 * @return JSONObject the reply
	 * @throws IOException
	 * @throws ParseException
	 */
	private JSONObject sendAndReceive(String jsonBody) throws IOException, ParseException {
		Socket socket = new Socket(InetAddress.getByName(WEBSERVER_ADDRESS), WEBSERVER_PORT);
		
		/* Send */
		DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		PrintWriter writer = new PrintWriter(output);
		writer.println(jsonBody);
		writer.flush();
		
		/* Receive */
		DataInputStream input = new DataInputStream(socket.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		JSONObject result = (JSONObject) JSON_PARSER.parse(reader.readLine());
		
		socket.close();
		
		return result;
	}
	
	public static void main(String [] args) {
		BiMap<String, String> rf = HashBiMap.create();
		rf.put("data", "full");
		
		LibrarianService ls = new LibrarianService();
		
		try {
			ls.list(rf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
