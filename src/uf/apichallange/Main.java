package uf.apichallange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.sound.sampled.ReverbType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Main {

	public static void main(String[] args) throws JSONException, ParseException {
		// Get Token
		String email = "ubaldo.franco@yahoo.com";
		String github= "https://github.com/uafranco2013/CODE2040Project";
		JSONObject sendData = new JSONObject();
		JSONObject tokenData = new JSONObject();
		String data;
		sendData.put("email", email);
		sendData.put("github", github);
		data = getData("http://challenge.code2040.org/api/register",sendData.toString());
		JSONObject root = new JSONObject(data);
		String token = root.getString("result");
		tokenData.put("token", token);
		//Reverse String
		/**
		data = getData("http://challenge.code2040.org/api/getstring", tokenData.toString());	
		root = new JSONObject(data);
		String reverse = reverseString(root.getString("result"));
		sendData = new JSONObject();
		sendData.put("token", token);
		sendData.put("string", reverse);
		System.out.println(root.get("result"));
		System.out.println(reverse);	
		data = getData("http://challenge.code2040.org/api/validatestring", sendData.toString());
		System.out.println(data);
		//Find needle in haystack
		data = getData("http://challenge.code2040.org/api/haystack", tokenData.toString());
		System.out.println(data);
		root = new JSONObject(data);
		JSONObject result = root.getJSONObject("result");
		JSONArray haystack = result.getJSONArray("haystack");
		String needle = result.getString("needle");
		System.out.println(needle + haystack.toString());
		int location = findWord(needle, haystack);
		System.out.println(location);
		sendData = new JSONObject();
		sendData.put("token", token);
		sendData.put("needle", location);
		data = getData("http://challenge.code2040.org/api/validateneedle", sendData.toString());
		System.out.println(data);
		**/	
		//Prefix
		data = getData("http://challenge.code2040.org/api/prefix", tokenData.toString());
		System.out.println(data);
		root = new JSONObject(data);
		JSONObject result = root.getJSONObject("result");
		JSONArray list = result.getJSONArray("array");
		String prefix = result.getString("prefix");
		JSONArray dataArray = deletePrefix(prefix, list);
		System.out.println(dataArray.toString());
		sendData = new JSONObject();
		sendData.put("token", token);
		sendData.put("array", dataArray);
		data = getData("http://challenge.code2040.org/api/validateprefix", sendData.toString());
		System.out.println(data);
		
		//Dating Game
		data = getData("http://challenge.code2040.org/api/time", tokenData.toString());
		System.out.println(data);
		root =  new JSONObject(data);
		result = root.getJSONObject("result");
		int interval = result.getInt("interval");
		String dateStamp = result.getString("datestamp");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Date date = dateFormat.parse(dateStamp);
		Calendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(Calendar.SECOND,interval);
		date = gc.getTime();
		String finalDate= dateFormat.format(date).toString();
		System.out.println(finalDate);
		sendData = new JSONObject();
		sendData.put("token", token);
		sendData.put("datestamp", finalDate);
		data = getData("http://challenge.code2040.org/api/validatetime", sendData.toString());
		System.out.println(data);
		
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
	
}
