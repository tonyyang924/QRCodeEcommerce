package com.tony.qrcodeecommerce.utils;

import org.json.JSONArray;

public class MyOrder implements java.io.Serializable {

    private static final String TAG = "MyOrder";

    /**
     * oid:訂單編號、order_price:訂單總金額
     * rname:收貨人名字、rphone:收貨人電話、remail:收貨人電子信箱
     * tplace:交易地點、ttime:交易時間、tupdate:訂單更新時間
     *
     * spec:規格、num:數量、pid:商品編號(1個商品 or 多個商品)
     */

    private String oid;
    private int oprice;
    private JSONArray orderItemArr;
    private String rname,rphone,remail;
    private String tplace,ttime,tupdate;
    private int situation;

    public MyOrder(String oid,int oprice,JSONArray orderItemArr,String rname,String rphone,String remail,
                   String tplace,String ttime,String tupdate,int situation) {
        this.oid = oid;
        this.oprice = oprice;
        this.orderItemArr = orderItemArr;
        this.rname = rname;
        this.rphone = rphone;
        this.remail = remail;
        this.tplace = tplace;
        this.ttime = ttime;
        this.tupdate = tupdate;
        this.situation = situation;
    }

    public String getOid() {
        return oid;
    }

    public int getOprice() {
        return oprice;
    }

    public JSONArray getOrderItemArr() {
        return orderItemArr;
    }

    public String getRname() {
        return rname;
    }

    public String getRphone() {
        return rphone;
    }

    public String getRemail() {
        return remail;
    }

    public String getTplace() {
        return tplace;
    }

    public String getTtime() {
        return ttime;
    }

    public String getTupdate() {
        return tupdate;
    }

    public int getSituation() {
        return situation;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public void setOprice(int oprice) {
        this.oprice = oprice;
    }

    public void setOrderItemArr(JSONArray orderItemArr) {
        this.orderItemArr = orderItemArr;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public void setRphone(String rphone) {
        this.rphone = rphone;
    }

    public void setRemail(String remail) {
        this.remail = remail;
    }

    public void setTplace(String tplace) {
        this.tplace = tplace;
    }

    public void setTtime(String ttime) {
        this.ttime = ttime;
    }

    public void setTupdate(String tupdate) {
        this.tupdate = tupdate;
    }

    public void setSituation(int situation) {
        this.situation = situation;
    }
}