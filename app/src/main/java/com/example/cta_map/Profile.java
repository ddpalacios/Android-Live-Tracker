package com.example.cta_map;

public class Profile {
    String name;
    String email;
    String phone;
    String bday;
    public Profile(String name, String email, String phone, String bday){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.bday = bday;
    }

    public String getBday() {
        return bday;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
