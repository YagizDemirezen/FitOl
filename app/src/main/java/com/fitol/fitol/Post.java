package com.fitol.fitol;

public class Post
{
    private String username;
    private String messageText;
    private String imageUrl;




    public Post(String username, String messageText, String imageUrl) {
        this.username = username;
        this.messageText = messageText;
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



}
