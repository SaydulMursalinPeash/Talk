package com.example.talk;

public class User {
    public String userName,email,profile_picture;

    public User() {
    }

    public User(String userName, String email, String profile_picture) {
        this.userName = userName;
        this.email = email;
        this.profile_picture = profile_picture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }
}
