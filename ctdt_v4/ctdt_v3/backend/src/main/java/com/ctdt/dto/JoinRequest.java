package com.ctdt.dto;
import jakarta.validation.constraints.NotBlank;
public class JoinRequest {
    @NotBlank private String joinToken;
    @NotBlank private String username;
    @NotBlank private String password;
    public String getJoinToken(){return joinToken;} public void setJoinToken(String v){this.joinToken=v;}
    public String getUsername(){return username;} public void setUsername(String v){this.username=v;}
    public String getPassword(){return password;} public void setPassword(String v){this.password=v;}
}
