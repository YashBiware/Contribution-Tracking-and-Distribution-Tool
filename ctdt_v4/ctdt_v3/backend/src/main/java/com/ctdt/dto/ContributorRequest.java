package com.ctdt.dto;
import jakarta.validation.constraints.*;
public class ContributorRequest {
    @NotBlank @Size(max=255) private String contributorName;
    private String username;
    private String password;
    public String getContributorName(){return contributorName;} public void setContributorName(String v){this.contributorName=v;}
    public String getUsername(){return username;} public void setUsername(String v){this.username=v;}
    public String getPassword(){return password;} public void setPassword(String v){this.password=v;}
}
