package com.my.soup.utils;

import com.my.soup.constants.ScrapeConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FJ24ScrapeUtil {
    public static Map<Integer,Set<String>> scrapeMainPageUrls(List<String> URLS, String[] filters) {
        Document doc = null;
        Map<Integer,Set<String>> navigatorUrlsMap = new HashMap<>();

        try{
            int pageCount = 1;
            for(String mainUrl : URLS){
                Set<String> navigatorUrls = new HashSet<>();
                doc = Jsoup.connect(mainUrl).get();
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String str = link.attr("abs:href");
                    if(!filterUrl(str, filters)){
                        navigatorUrls.add(str);
                    }
                }
                navigatorUrlsMap.put(pageCount++, navigatorUrls);
            }
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
        return navigatorUrlsMap;
    }

    public static Map<String,String> scrapeData(String URL) {
        Document doc = null;
        Map<String, String> scrapedDataMap = new HashMap<>();
        try{
            doc = Jsoup.connect(URL).get();
            Element abbrElement = doc.select("abbr").first();
            Elements links = doc.select("a[href]");
            String postTitle = doc.select("h1.post-title").text();
            scrapedDataMap.put("postTitle", postTitle);

            Element image = doc.select("img.size-full").first();

            //remove all unwanted divs
            doc.select("div.code-block").remove();
            doc.select("p.no-break").remove();
            doc.select("p>a[href]").remove();
            Element masthead = doc.select("div.entry-content").first();
            Elements paras = masthead.select("p");
            scrapedDataMap.put("Summary", paras.first().text());
            scrapeDate(scrapedDataMap, abbrElement);
            scrapeParas(scrapedDataMap, paras);
            scrapeApplyUrls(scrapedDataMap, links);
            scrapeNavURL(scrapedDataMap, URL);
            scrapeImage(scrapedDataMap, image);

        } catch(IOException ioe){
            ioe.printStackTrace();
        }
        return scrapedDataMap;
    }

    private static void scrapeImage(Map<String, String> scrapedDataMap, Element image) throws IOException {
        String url = image.attr("src");
        if(Objects.isNull(url)){
            scrapedDataMap.put("image","");
        }else{
            byte[] imageBytes = IOUtils.toByteArray(new URL(url));
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            scrapedDataMap.put("image","data:image/png;base64,"+base64);
        }
    }

    private static void scrapeParas(Map<String, String> scrapedDataMap, Elements paras) {
        String previousKey = "";
        for (int i = 1; i<paras.size(); i++) {
            Element para = paras.get(i);
            String pString = para.text();
            System.out.println(pString);
            StringBuffer lisString = new StringBuffer();

            String key = pString.split(":")[0].trim();
            Element pUlSibling = para.nextElementSibling();

            Elements strongs = para.select("strong");
            if( strongs.size() == 0 || pString.contains("Website:")){
                key = previousKey;
                lisString.append(scrapedDataMap.get(key)).append(para.text()).append("\n");
                scrapedDataMap.put(key, lisString.toString());
                continue;
            }
            if(!StringUtils.isBlank(pString) && pString.indexOf(":") != -1 && !pString.trim().endsWith(":")){
                lisString.append(pString.split(":")[1].trim()).append("\n");
            }
            if(null != pUlSibling && pUlSibling.nextElementSibling() != null && pUlSibling.tagName().equalsIgnoreCase("ul")){
                Elements lis = pUlSibling.select("li"); // select all li from ul
                for (Element li : lis) {
                    lisString.append(li.text()).append("\n");
                }
            }
            previousKey = key;
            scrapedDataMap.put(key, lisString.toString());
        }
    }

    public static void scrapeApplyUrls(Map<String, String> scrapedDataMap,  Elements links){
        StringBuffer applyUrls = new StringBuffer();
        for (Element link : links) {
            String linkText = link.text();
            if(linkText.toLowerCase().contains(ScrapeConstants.APPLY_FOR_LINKS_FILTER)){
                applyUrls.append(link.attr("abs:href")).append(" - ");
            }
        }
        scrapedDataMap.put("How To Apply", applyUrls.toString());
    }

    private static void scrapeDate(Map<String, String> scrapedDataMap, Element abbrElement) {
        String date = abbrElement.attr("title").substring(0,10);
        date = dateFormatter(date.substring(0,10));
        scrapedDataMap.put("Date",date);
    }

    public static void scrapeNavURL(Map<String, String> scrapedDataMap, String URL){
        URL = URL.replace("http://freshersjobs24.com/", "");
        URL = URL.replace("/", "");
        scrapedDataMap.put("Navigation",URL);
    }

    public static void writeToJson(List<Map<String, String>> mapData, String fileName){
        //Write JSON file
        JSONArray array = new JSONArray();
        array.addAll(mapData);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobs",array);
        try {
            FileWriter file = new FileWriter(fileName, false);
            file.write(jsonObject.toJSONString());
            file.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean filterUrl(String url, String[] filters){
        for(String str: filters){
            if(url.contains(str)){
                return true;
            } else if(url.endsWith("com") || url.endsWith("com/")) {
                return true;
            }
        }
        return false;
    }

    public static String dateFormatter(String inputDateStr){
        String outputDateStr = "";
        try{
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = inputFormat.parse(inputDateStr);
            outputDateStr = outputFormat.format(date);
        } catch(ParseException pe){
        }
        return outputDateStr;

    }

    public static int totalDataFiles() {
        File root = new File(".");
        String fileName = "data";
        int fileCount = 0;
        try {
            Collection files = FileUtils.listFiles(root, null, false);

            for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                File file = (File) iterator.next();
                if (file.getName().startsWith(fileName))
                    fileCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileCount;
    }

}
