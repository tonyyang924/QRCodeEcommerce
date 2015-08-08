package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.tony.qrcodeecommerce.utils.Item;
import com.tony.qrcodeecommerce.utils.ItemDAO;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartUserOrderActivity extends Activity {
    private MainApplication m;
    private Button submit,pickDateButton;
    private EditText userOrderNameEt,userOrderTelphoneEt,userOrderEmailEt;
    private Spinner userOrderPlaceSp;
    private TextView totalPriceTv;
    private static TextView userorderTransactionhourTv;
    private List<Item> lists;
    private ItemDAO itemDAO;
    private static int hourOfDay=0,minute=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartuserorder);
        m = (MainApplication) getApplication();
        userOrderNameEt = (EditText) findViewById(R.id.userorder_name);
        userOrderTelphoneEt = (EditText) findViewById(R.id.userorder_telphone);
        userOrderEmailEt = (EditText) findViewById(R.id.userorder_email);
        userOrderEmailEt.setText(String.format(getString(R.string.email_nkfust),m.getLoginUserId()));
        userOrderPlaceSp = (Spinner) findViewById(R.id.userorder_place);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.userorder_place_spinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userOrderPlaceSp.setAdapter(spinner_adapter);
        pickDateButton = (Button) findViewById(R.id.userorder_transactionhour);
        pickDateButton.setOnClickListener(pickDateClkLis);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(submitClkLis);
        totalPriceTv = (TextView) findViewById(R.id.userorder_totalprice_tv);
        userorderTransactionhourTv = (TextView) findViewById(R.id.userorder_transactionhour_tv);
        userorderTransactionhourTv.setText(Html.fromHtml(
                String.format(getString(R.string.userorder_transactionhour_text),hourOfDay,minute)));
        itemDAO = new ItemDAO(getApplicationContext());
        lists = itemDAO.getAll();
        //總金額
        int totalPrice = 0;
        for (Item list : lists) {
            totalPrice += list.getPrice() * list.getNumber();
        }
        totalPriceTv.setText(Html.fromHtml(String.format(getString(R.string.userorder_totalprice), totalPrice)));
    }

    private View.OnClickListener pickDateClkLis = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //選擇時間
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), "timePicker");
        }
    };

    private View.OnClickListener submitClkLis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //送出訂單
            //http://mobile.dennychen.tw/mobile_order_insert.php?pid=A&username=Tony&userphone=0912345678
            // &useremail=u0324813@nkfust.edu.tw&tplace=%E5%AD%B8%E6%A0%A1
            // &ttime=%E4%B8%8B%E5%8D%88&devicetoken=1313213122
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String urlstr = "http://mobile.dennychen.tw/mobile_order_insert.php";

                        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                        String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("pid","測試");
                        params.put("username",userOrderNameEt.getText().toString());
                        params.put("userphone",userOrderTelphoneEt.getText().toString());
                        params.put("useremail",userOrderEmailEt.getText().toString());
                        params.put("tplace",userOrderPlaceSp.getSelectedItem().toString());
                        params.put("ttime", String.format(getResources().getString(R.string.userorder_params_ttime),
                                2015, 8, 8, 9, 15));
                        params.put("devicetoken", token);

                        if(m.getTool().submitPostData(urlstr, params, "utf-8").equals("success")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), getString(R.string.userorder_submit_success_text), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), getString(R.string.userorder_submit_fails_text), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            CartUserOrderActivity.hourOfDay = hourOfDay;
            CartUserOrderActivity.minute = minute;
            userorderTransactionhourTv.setText(Html.fromHtml(
                    String.format(getString(R.string.userorder_transactionhour_text),hourOfDay,minute)));
        }
    }
}
