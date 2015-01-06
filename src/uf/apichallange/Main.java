package uf.apichallange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @author Ubaldo Franco
 * API Challange was a good review 
 * I did encounter a couple of times a needle was not provided for Stage 2
 * Stage 4 was a little more of challenge mostly due to finding the proper way to format the date and time provided
 */

public class Main {

	public static void main(String[] args) throws JSONException, ParseException {
		Scanner keyboard = new Scanner(System.in);
		// Get Token
		String email = "ubaldo.franco@yahoo.com";
		String github= "https://github.com/uafranco2013/CODE2040Project";
		String data;
		int choice = 0;
		JSONObject sendData = new JSONObject(); //used when sending answers
		JSONObject tokenData = new JSONObject(); //used only when token is required
		JSONObject root = new JSONObject(); 
		JSONObject result = new JSONObject();
		sendData.put("email", email);
		sendData.put("github", github);
		data = getData("http://challenge.code2040.org/api/register",sendData.toString());
		root = new JSONObject(data);
		String token = root.getString("result");
		tokenData.put("token", token);
		//Give Choices
		do{
		System.out.println("Choose an action: ");
		System.out.println("1: Reverse String");
		System.out.println("2: Needle in a haystack");
		System.out.println("3: Prefix");
		System.out.println("4: The Dating Game");
		System.out.println("5: Check Status");
		System.out.println("Any other number quits the program");
		choice = keyboard.nextInt();
		
		
		if(choice == 1){ //Reverse String
			data = getData("http://challenge.code2040.org/api/getstring", tokenData.toString());	
			root = new JSONObject(data);
			String reverse = reverseString(root.getString("result"));
			sendData = new JSONObject();
			sendData.put("token", token);
			sendData.put("string", reverse);	
			data = getData("http://challenge.code2040.org/api/validatestring", sendData.toString());
			System.out.println("String: " + root.getString("result") + " Reversed: " + reverse);
			System.out.println(new JSONObject(data).get("result"));
		}else if(choice == 2){ //Find needle in haystack
			data = getData("http://challenge.code2040.org/api/haystack", tokenData.toString());
			System.out.println(data);
			root = new JSONObject(data);
			result = root.getJSONObject("result");
			String needle = "";
			if(result.has("needle")){ 
				needle = result.getString("needle");
			}
			else{  //Very rarely a needle is not provided 
				System.out.println("No needle found this time, please try again");
				continue;
			}
				
			JSONArray haystack = result.getJSONArray("haystack");
			int location = findWord(needle, haystack);
			sendData = new JSONObject();
			sendData.put("token", token);
			sendData.put("needle", location);
			data = getData("http://challenge.code2040.org/api/validateneedle", sendData.toString());
			System.out.println("Needle: " + needle + "\nHaystack: " + haystack.toString()+ "\nindex:" + location);
			System.out.println(new JSONObject(data).get("result"));
		}else if(choice == 3){ //Prefix
			data = getData("http://challenge.code2040.org/api/prefix", tokenData.toString());
			root = new JSONObject(data);
			result = root.getJSONObject("result");
			JSONArray list = result.getJSONArray("array");
			String prefix = result.getString("prefix");
			JSONArray dataArray = deletePrefix(prefix, list);
			sendData = new JSONObject();
			sendData.put("token", token);
			sendData.put("array", dataArray);
			data = getData("http://challenge.code2040.org/api/validateprefix", sendData.toString());
			System.out.println("Old Array: " + list.toString() + "\nPrefix: " + prefix + "\nNew Array: " + dataArray.toString());
			System.out.println(new JSONObject(data).get("result"));
		}else if(choice == 4){ //Dating Game
			data = getData("http://challenge.code2040.org/api/time", tokenData.toString());
			root =  new JSONObject(data);
			result = root.getJSONObject("result");
			int interval = result.getInt("interval");
			String dateStamp = result.getString("datestamp");
			String finalDate = updateDate(dateStamp, interval);		
			sendData = new JSONObject();
			sendData.put("token", token);
			sendData.put("datestamp", finalDate);
			System.out.println("Starting time: " + dateStamp + " Interval: " + interval + " Ending time: " + finalDate);
			data = getData("http://challenge.code2040.org/api/validatetime", sendData.toString());
			System.out.println(new JSONObject(data).get("result"));
		}else if(choice == 5){ //Status Check
			data = getData("http://challenge.code2040.org/api/status", tokenData.toString());
			root = new JSONObject(data);
			result = root.getJSONObject("result");
			System.out.println("Stage 1 Passed: " + result.get("Stage 1 passed"));
			System.out.println("Stage 2 Passed: " + result.get("Stage 2 passed"));
			System.out.println("Stage 3 Passed: " + result.get("Stage 3 passed"));
			System.out.println("Stage 4 Passed: " + result.get("Stage 4 passed"));
		}
		}while(choice > 0 && choice < 6);
	}
	
	//Sending and Receiving Data
	public static String getData(String urlString, String sendData)
	{
		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.connect();
			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
			osw.write(sendData);
			osw.flush();
			osw.close();
			int statusCode = con.getResponseCode();
			if(statusCode  == HttpURLConnection.HTTP_OK);{
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = reader.readLine();
				while(line != null){
					sb.append(line);
					line = reader.readLine();
				}
				return sb.toString();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//Stage I: Reverse a string 
	public static String reverseString(String word){
		if(word == null)
			return "";
		String rWord = "";
		for(int i = word.length(); i > 0; i--){
			rWord = rWord + word.charAt(i - 1);
		}
		return rWord;
	}

	//Stage II: Needle in a haystack
	public static int findWord(String needle, JSONArray haystack) throws JSONException{
	
		for(int i = 0; i < haystack.length(); i++){
			if(haystack.get(i).equals(needle))
				return i;
		}
		
		return 0;
	}
	
	//Stage III: Prefix
	public static JSONArray deletePrefix(String prefix, JSONArray list) throws JSONException{
		JSONArray newList = new JSONArray();
		for(int i = 0; i < list.length(); i++)
		{
			if(!list.get(i).toString().contains(prefix))
				newList.put(list.get(i).toString());
		}
		return newList;
	}
	
	//Stage IV: The dating game
	public static String updateDate(String dateStamp, int interval) throws ParseException{	
		DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
		ZonedDateTime date = ZonedDateTime.parse(dateStamp, df);
		date = date.plusSeconds(interval);
		return date.toString();
	}
}
