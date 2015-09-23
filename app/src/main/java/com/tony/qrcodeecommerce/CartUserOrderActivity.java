package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.tony.qrcodeecommerce.utils.Item;
import com.tony.qrcodeecommerce.utils.ItemDAO;
import com.tony.qrcodeecommerce.utils.ProfileSP;
import com.tony.qrcodeecommerce.utils.SecurityCode;
import com.tony.qrcodeecommerce.utils.Tool;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartUserOrderActivity extends Activity {
    private static final String TAG = "CartUserOrderActivity";
    private Button submit, pickTimeButton;
    private EditText userOrderNameEt,userOrderTelphoneEt,userOrderEmailEt;
    private Spinner userOrderPlaceSp;
    private TextView totalPriceTv;
    private static TextView userorderTTimeTv;
    private List<Item> lists;
    private ItemDAO itemDAO;

    //checkbox
    private CheckBox checkbox;

    //使用者選擇的交易日期與時間
    private static int year = 0, monthOfYear = 0, dayOfMonth = 0, hourOfDay = 0, minute = 0;

    //layout
    private static View cartuserorder_time_Layout = null;
    private AlertDialog dialog = null;

    //驗證碼
    private String getCode;

    //
    private ProfileSP profileSP;

    //取得所有訂購之商品
    private JSONArray getOrderItems() {
        ArrayList<HashMap<String,Object>> theOrderItems = new ArrayList<>();
        ItemDAO itemDAO = new ItemDAO(this);
        List<Item> lists = itemDAO.getAll();
        for(Item list : lists) {
            HashMap<String,Object> orderItem = new HashMap<>();
            orderItem.put("oid", Tool.getOrderId(getApplicationContext(),getCode));
            orderItem.put("pid", list.getPid());
            orderItem.put("num", list.getNumber());
            orderItem.put("spec", list.getSpec());
            theOrderItems.add(orderItem);
        }
        JSONArray jsonArray = new JSONArray(theOrderItems);
        return jsonArray;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartuserorder);
        profileSP = new ProfileSP(getApplicationContext());
        userOrderNameEt = (EditText) findViewById(R.id.userorder_name);
        userOrderTelphoneEt = (EditText) findViewById(R.id.userorder_telphone);
        userOrderEmailEt = (EditText) findViewById(R.id.userorder_email);
        userOrderPlaceSp = (Spinner) findViewById(R.id.userorder_place);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    userOrderNameEt.setText(profileSP.getUserProfile().getStuName());
                    userOrderTelphoneEt.setText(profileSP.getUserProfile().getStuPhone());
                    userOrderEmailEt.setText(profileSP.getUserProfile().getStuEmail());
                } else {
                    userOrderNameEt.setText("");
                    userOrderTelphoneEt.setText("");
                    userOrderEmailEt.setText("");
                }
            }
        });
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.userorder_place_spinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userOrderPlaceSp.setAdapter(spinner_adapter);
        pickTimeButton = (Button) findViewById(R.id.userorder_ttime_btn);
        pickTimeButton.setOnClickListener(pickTimeClkLis);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(submitClkLis);
        totalPriceTv = (TextView) findViewById(R.id.userorder_totalprice_tv);
        userorderTTimeTv = (TextView) findViewById(R.id.userorder_ttime_tv); //使用者選擇的時間
        itemDAO = new ItemDAO(getApplicationContext());
        lists = itemDAO.getAll();
        //總金額
        int totalPrice = 0;
        for (Item list : lists) {
            totalPrice += list.getPrice() * list.getNumber();
        }
        totalPriceTv.setText(Html.fromHtml(String.format(getString(R.string.userorder_totalprice), totalPrice)));
    }

    private View.OnClickListener pickTimeClkLis = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i(TAG,"pickTimeClkLis");

            LayoutInflater inflater = getLayoutInflater();
            cartuserorder_time_Layout = inflater.inflate(R.layout.cartuserorder_time, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(CartUserOrderActivity.this);
            builder.setView(cartuserorder_time_Layout);
            builder.setCancelable(true);
            builder.create();
            dialog = builder.show();

            Button datepickerbtn = (Button)cartuserorder_time_Layout.findViewById(R.id.datepickerbtn);
            datepickerbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //選擇日期
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(), "datePicker");
                }
            });
            Button timepickerbtn = (Button)cartuserorder_time_Layout.findViewById(R.id.timepickerbtn);
            timepickerbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //選擇時間
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                }
            });
            Button timesubmitbtn = (Button)cartuserorder_time_Layout.findViewById(R.id.timesubmitbtn);
            timesubmitbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //關閉視窗與顯示日期與時間在TextView上
                    userorderTTimeTv.setText(Html.fromHtml(
                            String.format(getResources().getString(R.string.userorder_ttime_text), year, monthOfYear, dayOfMonth, hourOfDay, minute)));
                    dialog.dismiss();
                }
            });
        }
    };

    private View.OnClickListener submitClkLis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(MainApplication.DEBUG) { //測試時略過驗證碼，但仍需取得驗證碼，為了oid
                getCode = SecurityCode.getInstance().getCode(false);
                DoThread();
            } else { //實測時需輸入驗證碼
                //送出訂單
                //http://mobile.dennychen.tw/mobile_order_insert.php?pid=A&username=Tony&userphone=0912345678
                // &useremail=u0324813@nkfust.edu.tw&tplace=%E5%AD%B8%E6%A0%A1
                // &ttime=%E4%B8%8B%E5%8D%88&devicetoken=1313213122
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.securitycode, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(CartUserOrderActivity.this);
                builder.setView(layout);
                builder.setCancelable(true);
                builder.create();
                dialog = builder.show();

                // 獲取顯示的驗證碼
                getCode = SecurityCode.getInstance().getCode(false);

                final ImageView vc_image = (ImageView) layout.findViewById(R.id.verify_imv);
                final EditText vc_code = (EditText) layout.findViewById(R.id.myedit);
                final Button vc_shuaxin = (Button) layout.findViewById(R.id.vc_shuaxin);
                vc_shuaxin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vc_image.setImageBitmap(SecurityCode.getInstance().getBitmap());
                        getCode = SecurityCode.getInstance().getCode(false);
                    }
                });
                final Button verfiy_btn = (Button) layout.findViewById(R.id.verfiy_btn);
                verfiy_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String verfiyString = vc_code.getText().toString();
                        if (verfiyString.equals(getCode)) {
                            Toast.makeText(getApplicationContext(), "驗證碼輸入正確", Toast.LENGTH_LONG).show();
                            DoThread();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "驗證碼輸入錯誤", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    };

    private void DoThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                                        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                    String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                    Map<String, String> params = new HashMap<String, String>();
                    Log.i(TAG, "oid:" + Tool.getOrderId(getApplicationContext(), getCode));
                    params.put("proc","order_insert");
                    params.put("oid", Tool.getOrderId(getApplicationContext(), getCode));
                    params.put("username",userOrderNameEt.getText().toString());
                    params.put("userphone",userOrderTelphoneEt.getText().toString());
                    params.put("useremail",userOrderEmailEt.getText().toString());
                    params.put("tplace",userOrderPlaceSp.getSelectedItem().toString());
                    params.put("ttime", String.format(getResources().getString(R.string.userorder_params_ttime),
                            year, monthOfYear, dayOfMonth, hourOfDay, minute));
                    params.put("devicetoken", token);
                    params.put("orderitems",getOrderItems().toString()); //所有訂購的商品

                    String resultData = Tool.submitPostData(MainApplication.SERVER_PROC, params, "utf-8");

                    Log.i(TAG,"resultData:"+resultData);

                    //切割結果字串
                    String[] strArr = resultData.split(",");

                    if(strArr[0].equals("success")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getString(R.string.userorder_submit_success_text), Toast.LENGTH_LONG).show();
                            }
                        });
                        Intent intent = new Intent(CartUserOrderActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        if(!MainApplication.DEBUG) {//如果不是測試，就刪除所有購物車資料
                            itemDAO.deleteAll();
                        }
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

    //日期
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, y, m, d);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            CartUserOrderActivity.year = year;
            CartUserOrderActivity.monthOfYear = monthOfYear + 1; //月都會少1所以加1
            CartUserOrderActivity.dayOfMonth = dayOfMonth;
            TextView date_tv = (TextView)cartuserorder_time_Layout.findViewById(R.id.date_tv);
            date_tv.setText(Html.fromHtml(
                    String.format(getResources().getString(R.string.userorder_inflater_date_text),
                            CartUserOrderActivity.year,CartUserOrderActivity.monthOfYear,
                            CartUserOrderActivity.dayOfMonth)));
        }
    }

    //時間
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
            TextView time_tv = (TextView)cartuserorder_time_Layout.findViewById(R.id.time_tv);
            time_tv.setText(Html.fromHtml(
                    String.format(getResources().getString(R.string.userorder_inflater_time_text),
                            CartUserOrderActivity.hourOfDay,
                            CartUserOrderActivity.minute)));
        }
    }
}
