package com.my.soup.service;

import com.my.soup.constants.ScrapeConstants;
import com.my.soup.utils.JsonReader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FJ24DataService {
    public List<Map<String, String>> readDataFromJson(){
        return JsonReader.readJsonFromFile(ScrapeConstants.FJ24_DEFAULT_JSON_FILE_PATH, ScrapeConstants.FJ24_JSON_FILE_PATH_ARR_NAME);
    }
}
