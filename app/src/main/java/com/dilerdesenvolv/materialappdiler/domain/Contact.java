package com.dilerdesenvolv.materialappdiler.domain;

/**
 * Created by T-Gamer on 13/08/2016.
 */
public class Contact {

    private String email;
    private String subject;
    private String message;

    public Contact() {}
    public Contact(String email, String subject, String message) {
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

}
