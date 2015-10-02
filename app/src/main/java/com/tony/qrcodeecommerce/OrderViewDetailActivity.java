package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.tony.qrcodeecommerce.utils.MyOrder;

public class OrderViewDetailActivity extends Activity{
    private static final String TAG = "OrderViewDetailActivity";

    private TextView oid,rname,rphone,remail,tplace,ttime,oprice,tupdate;
    private MyOrder myOrder;

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

        myOrder = MainApplication.getMyOrder();

        oid.setText(String.format(getResources().getString(R.string.orderview_oid),myOrder.getOid()));
        rname.setText(String.format(getResources().getString(R.string.orderview_rname),myOrder.getRname()));
        rphone.setText(String.format(getResources().getString(R.string.orderview_rphone),myOrder.getRphone()));
        remail.setText(String.format(getResources().getString(R.string.orderview_remail),myOrder.getRemail()));
        tplace.setText(String.format(getResources().getString(R.string.orderview_tplace),myOrder.getTplace()));
        ttime.setText(String.format(getResources().getString(R.string.orderview_ttime),myOrder.getTtime()));
        oprice.setText(String.format(getResources().getString(R.string.orderview_oprice),myOrder.getOprice()));
        tupdate.setText(String.format(getResources().getString(R.string.orderview_tupdate),myOrder.getTupdate()));
    }
}
