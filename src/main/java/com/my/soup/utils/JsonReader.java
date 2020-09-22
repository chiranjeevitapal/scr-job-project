package com.my.soup.utils;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonReader {
    public static JSONArray readJsonFromUrl(String url) {

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            URL readURL = new URL(url); // URL to Parse
            URLConnection yc = readURL.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                jsonArray = (JSONArray) parser.parse(inputLine);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }


    public static List<Map<String, String>> readJsonFromFile(String fileName, String arrayName) {
        JSONParser parser = new JSONParser();
        List<Map<String, String>> data = new ArrayList<>();
        try {
            Object obj = parser.parse(new FileReader(fileName));
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray jobsList = (JSONArray) jsonObject.get(arrayName);

            Iterator<JSONObject> iterator = jobsList.iterator();
            while (iterator.hasNext()) {
                data.add(iterator.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
