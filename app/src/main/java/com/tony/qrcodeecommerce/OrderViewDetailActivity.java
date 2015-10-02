package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.tony.qrcodeecommerce.utils.MyOrder;
import com.tony.qrcodeecommerce.utils.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class OrderViewDetailActivity extends Activity{
    private static final String TAG = "OrderViewDetailActivity";

    private TextView oid,rname,rphone,remail,tplace,ttime,oprice,tupdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderview_detail);
        oid = (TextView) findViewById(R.id.oid);
        rname = (TextView) findViewById(R.id.rname);
        rphone = (TextView) findViewById(R.id.rphone);
        remail = (TextView) findViewById(R.id.remail);
        tplace = (TextView) findViewById(R.id.tplace);
        ttime = (TextView) findViewById(R.id.ttime);
        oprice = (TextView) findViewById(R.id.oprice);
        tupdate = (TextView) findViewById(R.id.tupdate);

    }

    private MyOrder DeserializingMyOrder() {
        MyOrder myOrder = null;
        try {
            FileInputStream fis = new FileInputStream(Tool.QRCodeEcommercePath + "/tempdata.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            myOrder = (MyOrder) ois.readObject();
            ois.close();
            // Clean up the file
            new File("tempdata.ser").delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myOrder;
    }
}
