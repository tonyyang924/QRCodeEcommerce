package com.tony.qrcodeecommerce.utils;

/**
 * Product用來放所有商品的資料
 */

public class Product implements java.io.Serializable{
    //
    private long id;            //絕對編號
    private String pid;         //編號
    private String name;        //產品名稱
    private int price;          //價格
    private String pic;         //圖片名稱
    private String picLink;    //圖片網址
    private String link;        //商品網址

    public Product() {}
    public Product(long id,String pid,String name,int price,
                   String pic,String picLink,String link) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.price = price;
        this.pic = pic;
        this.picLink = picLink;
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPicLink() {
        return picLink;
    }

    public void setPicLink(String picLink) {
        this.picLink = picLink;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
