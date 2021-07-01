package net.sourov.mycontact.Model;

public class Contacts {

    String name,number,dateOfBirth,address,imageUrl,uniqueID;

    public Contacts(String name, String number, String dateOfBirth, String address, String imageUrl, String uniqueID) {
        this.name = name;
        this.number = number;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.imageUrl = imageUrl;
        this.uniqueID = uniqueID;
    }

    public Contacts() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
}
