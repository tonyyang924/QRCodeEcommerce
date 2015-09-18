package com.tony.qrcodeecommerce.utils;

import java.util.Date;
import java.util.Locale;

public class Item implements java.io.Serializable {
    //資料庫
    private long id;
    private String pid;
    private String name;
    private int price;
    private String pic;
    private String pic_link;
    private String link;
    private long datetime;
    /**
     * 購物車額外增加欄位
     */
    //此商品要幾個
    private int number = 1;
    // 此商品的規格，
    // 以衣服來說：尺寸為何(XS,S,M,L,XL)
    // 或是其他商品的大小等等。
    private String spec = "none";
    //此商品剩餘幾個
    private int limitNumber = 0;

    //建構元
    public Item() {
    }

    public Item(long id, String pid, String name, int price, String pic, String pic_link,
                String link, long datetime) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.price = price;
        this.pic = pic;
        this.pic_link = pic_link;
        this.link = link;
        this.datetime = datetime;
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

    public String getPic_link() {
        return pic_link;
    }

    public void setPic_link(String pic_link) {
        this.pic_link = pic_link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public int getLimitNumber() {
        return limitNumber;
    }

    public void setLimitNumber(int limitNumber) {
        this.limitNumber = limitNumber;
    }

    // 裝置區域的日期時間
    public String getLocaleDatetime() {
        return String.format(Locale.getDefault(), "%tF  %<tR", new Date(datetime));
    }

    // 裝置區域的日期
    public String getLocaleDate() {
        return String.format(Locale.getDefault(), "%tF", new Date(datetime));
    }

    // 裝置區域的時間
    public String getLocaleTime() {
        return String.format(Locale.getDefault(), "%tR", new Date(datetime));
    }
}
