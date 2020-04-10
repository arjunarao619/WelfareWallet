package com.arjunrao.welfarewallet;

/**
 * Created by Belal on 3/18/2018.
 */

public class Product {

    //private String image;
    private String title, shortdesc;
    private String price;
    private String rating;
    private String comments;
    private String balance;
    private boolean subsidy;
    public Product(){

    }


    public boolean isSubsidy() {
        return subsidy;
    }

    public Product(String title, String shortDesc, String price, String rating, String comments, String balance, boolean subsidy) {
        //this.image = image;
        this.title = title;
        this.shortdesc = shortDesc;
        this.price = price;
        this.rating = rating;
        this.comments = comments;
        this.balance = balance;
        this.subsidy = subsidy;
    }

  //  public String getImage() {
  //      return image;
  //  }

    public String getTitle() {
        return title;
    }

    public String getShortdesc() {
        return shortdesc;
    }

    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    public String getBalance() {
        return balance;
    }
}
