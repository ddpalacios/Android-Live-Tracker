package com.example.cta_map;

import android.util.Log;

public class Profile {
    String first;
    String last;

    String email;
    String phone;
    String bday;
    String username;
    String password;
    Integer id;

    public Profile(String first, String last, String username,String email) {
        this.first = first;
        Log.e("name", this.first);
        this.last = last;
        Log.e("name", this.last);

        this.username = username;
        this.id = null;


        this.email = email;
        this.phone = null;
        this.bday = null;
        this.password = null;
    }
    public void setID(Integer id){
        this.id = id;

    }
    public Integer getID(){
        return this.id;
    }

    public void setPassword(String password){
        this.password = toHex(password);
    }


    public String getUsername(){
        return this.username;
    }

    public void setPhone(String phone){
        this.phone = phone;

    }
    public void setBday(String bday){
        this.bday = bday;
    }

    public String getPassword(){
        return this.password;
    }

    public String getFirst(){
        return this.first;
    }
    public String getLast(){
        return  this.last;
    }

    public String getPhone(){
       return this.phone;
    }
    public String getBday(){
        return this.bday;
    }
    public String getEmail(){
        return this.email;
    }



    private String toHex(String s){
        char[] ch = s.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (char c : ch) {
            String hexString = Integer.toHexString(c);
            sb.append(hexString);
        }
        return sb.toString();
    }

    private String toString(String hex) {
        StringBuilder result = new StringBuilder();
        char[] charArray = hex.toCharArray();
        for (int i = 0; i < charArray.length; i = i + 2) {
            String st = "" + charArray[i] + "" + charArray[i + 1];
            char ch = (char) Integer.parseInt(st, 16);
            result.append(ch);
        }

        return result.toString();

    }




}