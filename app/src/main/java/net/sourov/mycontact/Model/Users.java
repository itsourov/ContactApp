package net.sourov.mycontact.Model;

public class Users {

    String dateOFBirth,email,name,imageUrl,number;

    public Users(String dateOFBirth, String email, String name, String imageUrl, String number) {
        this.dateOFBirth = dateOFBirth;
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.number = number;
    }

    public Users() {
    }

    public String getDateOFBirth() {
        return dateOFBirth;
    }

    public void setDateOFBirth(String dateOFBirth) {
        this.dateOFBirth = dateOFBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
