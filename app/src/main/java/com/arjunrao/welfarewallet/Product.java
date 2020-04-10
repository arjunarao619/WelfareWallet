package com.arjunrao.welfarewallet;

/**
 * Created by Belal on 3/18/2018.
 */

public class Product {

    //private String image;
    private String title, shortdesc;
    private String price;
    private String rating;

    public Product(){

    }

    public Product(String title, String shortDesc, String price, String rating) {
        //this.image = image;
        this.title = title;
        this.shortdesc = shortDesc;
        this.price = price;
        this.rating = rating;
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
}
