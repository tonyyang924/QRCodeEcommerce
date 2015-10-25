package com.tony.qrcodeecommerce.utils;

public class Profile implements java.io.Serializable {
    //資料庫
    private String stu_id;      //帳號
    private String stu_name;    //名字
    private String stu_phone;   //電話
    private String stu_email;   //email
    //建構元
    public Profile() {

    }
    public Profile(String stu_id,String stu_name,String stu_phone,String stu_email) {
        this.stu_id = stu_id;
        this.stu_name = stu_name;
        this.stu_phone = stu_phone;
        this.stu_email = stu_email;
    }
    public String getStuId() {
        return stu_id;
    }
    public void setStuId(String stu_id) {
        this.stu_id = stu_id;
    }
    public String getStuName() {
        return stu_name;
    }
    public void setStuName(String stu_name) {
        this.stu_name = stu_name;
    }
    public String getStuPhone() {
        return stu_phone;
    }
    public void setStuPhone(String stu_phone) {
        this.stu_phone = stu_phone;
    }
    public String getStuEmail() {
        return stu_email;
    }
    public void setStuEmail(String stu_email) {
        this.stu_email = stu_email;
    }
}
