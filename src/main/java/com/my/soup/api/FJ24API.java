package com.my.soup.api;

import com.my.soup.constants.ScrapeConstants;
import com.my.soup.utils.FJ24ScrapeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("api")
public class FJ24API {
    Logger logger = LoggerFactory.getLogger(FJ24API.class);

    @GetMapping("/scrape/{name}")
    public String scrape(@PathVariable String name) {
        Map<Integer,Set<String>> navigatorURLSMap = new HashMap<>();


        //Get the actual URL to be scraped from main page
        if("fj24".equalsIgnoreCase(name)){
            navigatorURLSMap = FJ24ScrapeUtil.scrapeMainPageUrls(ScrapeConstants.FJ24_MAIN_URL, ScrapeConstants.FJ24_MAIN_URL_FILTERS);
        }
        //http://freshersjobs24.com/page/2/
        // Loop through the urls and scrape
        for(int i = 1; i<=navigatorURLSMap.size(); i++) {
            List<Map<String, String>> data = new ArrayList<>();
            Set<String> navigatorUrls = navigatorURLSMap.get(i);
            for (String url : navigatorUrls) {
                logger.info(url);
                Map<String, String>scrapedDataMap;
                scrapedDataMap = FJ24ScrapeUtil.scrapeData(url);
                data.add(scrapedDataMap);
            }
            logger.info("started writing data to json file");
            FJ24ScrapeUtil.writeToJson(data, "data"+i+".json");
            logger.info("ended writing data to json file");
        }
        return "done";
    }
}
