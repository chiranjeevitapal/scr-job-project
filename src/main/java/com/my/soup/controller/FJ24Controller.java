package com.my.soup.controller;

import com.my.soup.constants.ScrapeConstants;
import com.my.soup.service.FJ24DataService;
import com.my.soup.utils.FJ24ScrapeUtil;
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
        model.addAttribute("pages", FJ24ScrapeUtil.totalDataFiles());
        model.addAttribute("data", fJ24DataService.readDataFromJson(1));
        model.addAttribute("pageIndex", 1);
        return "home";
    }

    @GetMapping("/page/{count}")
    public String home(Model model, @PathVariable int count) {
        model.addAttribute("pages", FJ24ScrapeUtil.totalDataFiles());
        model.addAttribute("data", fJ24DataService.readDataFromJson(count));
        model.addAttribute("pageIndex", count);
        return "home";
    }


    // Method to get a single job
    @GetMapping("/job/{param}")
    public String job(Model model, @PathVariable String param) {
        logger.info("Param: " + param);
        Map<String, String> jobMap = new TreeMap<>();
        outerLoop:
        for(int i = 1; i<= FJ24ScrapeUtil.totalDataFiles(); i++ ) {
            List<Map<String, String>> data = fJ24DataService.readDataFromJson(i);
            for (Map dataMap : data) {
                logger.info("company: "+dataMap.get("Company Name"));
                if (dataMap.get("Navigation").equals(param)) {
                    jobMap.putAll(dataMap);
                    break outerLoop;
                }
            }
        }
        String[] howToApplyLinks = jobMap.get("How To Apply").split(" - ");
        model.addAttribute("howToApplyLinks", howToApplyLinks);
        model.addAttribute("job", jobMap);
        return "job";
    }

    // Method to search with keyword
    @GetMapping("/search/{param}")
    public String search(Model model, @PathVariable String param, String category) {
        logger.info("Param: " + param);
        List<Map<String, String>> jobsList = new ArrayList<>();
        for(int i=1; i<=FJ24ScrapeUtil.totalDataFiles(); i++ ) {
            List<Map<String, String>> data = fJ24DataService.readDataFromJson(i);
            for (Map dataMap : data) {
                StringBuffer buffer = new StringBuffer();
                if(category.equalsIgnoreCase("company")){
                    String companyName =  dataMap.get("Company Name") == null ? "" : (String) dataMap.get("Company Name");
                    if (companyName.toLowerCase().contains(param.toLowerCase())) {
                        jobsList.add(dataMap);
                    }
                } else if(category.equalsIgnoreCase("location")) {
                    String location = dataMap.get("Job Location") == null ? "" : (String) dataMap.get("Job Location");
                    if (location.toLowerCase().contains(param.toLowerCase())) {
                        jobsList.add(dataMap);
                    }
                } else{
                    dataMap.forEach((key, val) -> {
                        buffer.append(key).append(" ").append(val);
                    });
                }
            }
        }
        if(jobsList.size() == 0){
            model.addAttribute("data", "");
        }else{
            model.addAttribute("data", jobsList);
        }
        model.addAttribute("pages", 0);
        model.addAttribute("pageIndex", 1);

        return "home";
    }

    @GetMapping("/location/{param}")
    public String tag(Model model, @PathVariable String param) {
        if(param.startsWith("jobs-in-")){
            param = param.split("-")[2];
        }
        return search(model,param,"location");
    }

    @GetMapping("/company/{param}")
    public String category(Model model, @PathVariable String param) {
        if(param.startsWith("jobs-in-")){
            param = param.split("-")[2];
        }
        return search(model,param,"company");
    }
}
