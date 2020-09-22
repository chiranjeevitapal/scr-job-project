package com.my.soup.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class JobPosting {
    private String companyName;
    private String jobLocation;
    private String experience;
    private String jobCategory;
    private String jobType;
    private String jobPosition;
    private String qualification;
    private String yearOfPassing;
    private String eligibility;
    private String jobDescription;
    private String jobResponsibilities;
    private String howToApply;
}
