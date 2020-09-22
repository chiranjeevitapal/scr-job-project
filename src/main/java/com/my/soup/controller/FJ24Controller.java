package com.my.soup.controller;

import com.my.soup.constants.ScrapeConstants;
import com.my.soup.service.FJ24DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.*;

@Controller
public class FJ24Controller {
    Logger logger = LoggerFactory.getLogger(FJ24Controller.class);

    @Autowired
    private FJ24DataService fJ24DataService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalPages", ScrapeConstants.FJ24_MAIN_URL.length);
        model.addAttribute("data", fJ24DataService.readDataFromJson());
        return "home";
    }


    // Method to get a single job
    @GetMapping("/job/{param}")
    public String job(Model model, @PathVariable String param) {
        logger.info("Param: " + param);
        Map<String, String> jobMap = new TreeMap<>();
        List<Map<String, String>> data = fJ24DataService.readDataFromJson();
        for (Map dataMap : data) {
            if (dataMap.get("Navigation").equals(param)) {
                jobMap.putAll(dataMap);
                break;
            }
        }

        String[] howToApplyLinks = jobMap.get("How To Apply").split(" - ");
        model.addAttribute("howToApplyLinks", howToApplyLinks);
        model.addAttribute("job", jobMap);
        return "job";
    }

    // Method to search with keyword
    @GetMapping("/category/{param}")
    public String search(Model model, @PathVariable String param) {
        logger.info("Param: " + param);
        List<Map<String, String>> jobsList = new ArrayList<>();

        List<Map<String, String>> data = fJ24DataService.readDataFromJson();
        for (Map dataMap : data) {
            StringBuffer buffer = new StringBuffer();
            dataMap.forEach((key, val) -> {
                buffer.append(key).append(" ").append(val);
            });
            if (buffer.toString().toLowerCase().contains(param.toLowerCase())) {
                jobsList.add(dataMap);
            }
        }
        if(jobsList.size() == 0){
            model.addAttribute("data", "");
        }else{
            model.addAttribute("data", jobsList);
        }

        return "home";
    }
}
