package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.qrcodeecommerce.utils.Item;
import com.tony.qrcodeecommerce.utils.ItemDAO;
import com.tony.qrcodeecommerce.utils.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {
    private static final String TAG = "CartFragment";
    private ListView listView;
    private ItemDAO itemDAO;
    private MyAdapter adapter;
    private List<Item> lists;
    private Button submit;
    private TextView totalpriceTV;
    private RelativeLayout noItemLayout;

    //顯示文字內容
    private String text = "";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //取得MainActivity的方法，將文字放入text字串
        MainActivity mMainActivity = (MainActivity) activity;
        text = mMainActivity.getCartText();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //導入Tab分頁的Fragment Layout
        return inflater.inflate(R.layout.activity_cart, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        submit = (Button) getActivity().findViewById(R.id.button);
        submit.setOnClickListener(submitClkLis);
        listView = (ListView) getActivity().findViewById(R.id.listView);
        totalpriceTV = (TextView) getActivity().findViewById(R.id.totalpriceTV);
        itemDAO = new ItemDAO(getActivity());
        lists = itemDAO.getAll();
        adapter = new MyAdapter(getActivity());
        listView.setAdapter(adapter);
        noItemLayout = (RelativeLayout) getActivity().findViewById(R.id.noItemLayout);
        showNoItem();
        checkItemNumber();
    }

    private void checkItemNumber() {
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<lists.size();i++) {
            JSONObject json = new JSONObject();
            try {
                json.put("pid", lists.get(i).getPid());
                json.put("spec", lists.get(i).getSpec());
                jsonArray.put(json);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

        final String jsonArrayStr = jsonArray.toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlstr = "http://163.18.42.145/mobile/mobile_process.php";

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("proc", "product_search");
                    params.put("json", jsonArrayStr);
                    String responseMsg = Tool.submitPostData(urlstr, params, "utf-8");
                    //如果沒有error就是代表有回傳json格式的資料
                    if (!responseMsg.equals("error")) {
                        Log.i(TAG,"responseMsg: "+responseMsg);
                        JSONArray jsonArrayResponse =  new JSONArray(responseMsg);
                        for(int i=0;i<jsonArrayResponse.length();i++) {
                            Log.i(TAG,"object ===> "+jsonArrayResponse.getJSONObject(i));
                            String pid = jsonArrayResponse.getJSONObject(i).getString("pid");
                            String spec = jsonArrayResponse.getJSONObject(i).getString("spec");
                            String amount = jsonArrayResponse.getJSONObject(i).getString("amount");

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private final class MyView {
        public ImageView itemImg;
        public TextView itemName;
        public TextView numberTv;
        public TextView priceTv;
        public TextView subTotalTV;
        public ImageButton addNumberBtn;
        public ImageButton subNumberBtn;
        public Spinner specSp;
        public ImageButton delBtn;
    }

    // 實作一個 Adapter 繼承 BaseAdapter
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            //回傳這個 List 有幾個 item
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final MyView myviews = new MyView();
            convertView = inflater.inflate(R.layout.listview_cart, null);
            myviews.itemImg = (ImageView) convertView.findViewById(R.id.imageView);
            myviews.itemName = (TextView) convertView.findViewById(R.id.textView);
            myviews.numberTv = (TextView) convertView.findViewById(R.id.NumberTv);
            myviews.priceTv = (TextView) convertView.findViewById(R.id.priceTV);
            myviews.subTotalTV = (TextView) convertView.findViewById(R.id.subTotalTV);
            myviews.addNumberBtn = (ImageButton) convertView.findViewById(R.id.addNumberBtn);
            myviews.subNumberBtn = (ImageButton) convertView.findViewById(R.id.subNumberBtn);



            if(lists.get(position).getPid().indexOf("A") != -1) { //如果有A
                myviews.specSp = (Spinner) convertView.findViewById(R.id.specSp);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(getActivity(),
                        R.array.cart_size_spinner, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                myviews.specSp.setAdapter(spinner_adapter);
                myviews.specSp.setVisibility(View.VISIBLE);
                /**
                 * 判斷目前規格為何
                 */
                //取得所有規格(衣服size)
                String spec[] = getResources().getStringArray(R.array.cart_size_spinner);
                //取得此Item的規格
                String item_spec = lists.get(position).getSpec();
                Log.i(TAG,"item_spec:"+item_spec);
                //存放index的int變數
                int specIndex = 0;
                //跑迴圈找出此Item規格在spec array中的Index
                for(int i=0;i<spec.length;i++) {
                    if(spec[i].equals(item_spec)) {
                        specIndex = i;
                        break;
                    }
                }
                Log.i(TAG,"index:"+specIndex);
                //選擇目前規格
                myviews.specSp.setSelection(specIndex);

                myviews.specSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        //取得尺寸的預設名稱
                        String none = getResources().getStringArray(R.array.cart_size_spinner)[0];
                        //取得目前選擇的尺寸
                        String choiceSize = parent.getSelectedItem().toString();
                        //如果選項上依然是預設名稱
                        if (choiceSize.equals(none)) {
                            lists.get(position).setSpec("none");
                            Log.i(TAG, "尚未選擇尺寸。");
                        } else { //如果選擇尺寸
                            lists.get(position).setSpec(choiceSize);
                            Log.i(TAG, "你選擇:" + choiceSize);
                        }
                        itemDAOUpdate(lists.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            myviews.delBtn = (ImageButton) convertView.findViewById(R.id.delBtn);
            myviews.addNumberBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = lists.get(position).getNumber();
                    if (num + 1 <= 5)
                        num++;
                    lists.get(position).setNumber(num);
                    itemDAOUpdate(lists.get(position));
                    refreshItemValue(myviews, position);
                    refreshTotalPrice();
                }
            });
            myviews.subNumberBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = lists.get(position).getNumber();
                    if (num - 1 > 0)
                        num--;
                    lists.get(position).setNumber(num);
                    itemDAOUpdate(lists.get(position));
                    refreshItemValue(myviews, position);
                    refreshTotalPrice();
                }
            });
            myviews.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delDialog(position);
                }
            });
            String imgPath = Tool.QRCodeEcommercePath + "/images/" + lists.get(position).getPic();
            Bitmap bmp = BitmapFactory.decodeFile(imgPath);
            myviews.itemImg.setImageBitmap(bmp);
            myviews.itemName.setText(lists.get(position).getName());
            refreshItemValue(myviews, position);
            refreshTotalPrice();
            return convertView;
        }
    }

    private void refreshItemValue(MyView myviews, final int position) {
        //數量
        myviews.numberTv.setText("" + lists.get(position).getNumber());
        //價格
        myviews.priceTv.setText(Html.fromHtml(String.format(getResources().getString(R.string.cart_price),
                lists.get(position).getPrice())));
        //小計
        myviews.subTotalTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.cart_subtotal),
                lists.get(position).getNumber() * lists.get(position).getPrice())));
    }

    private void refreshTotalPrice() {
        //總金額
        int totalPrice = 0;
        for (Item list : lists) {
            totalPrice += list.getPrice() * list.getNumber();
        }
        totalpriceTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.cart_totalprice),
                totalPrice)));
    }

    private void showNoItem() {
        if (lists.size() <= 0) {
            //顯示沒有項目的文案
            noItemLayout.setVisibility(View.VISIBLE);
            //隱藏總金額及按鈕
            totalpriceTV.setVisibility(View.INVISIBLE);
            submit.setVisibility(View.INVISIBLE);
        } else {
            //隱藏沒有項目的文案
            noItemLayout.setVisibility(View.INVISIBLE);
            //顯示總金額及按鈕
            totalpriceTV.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
        }
    }

    //刪除dialog
    private void delDialog(final int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示訊息")
                .setMessage("確定要將商品從購物車刪除嗎?")
                .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        itemDAODelete(lists.get(position));
                        lists.clear();
                        lists = itemDAO.getAll();
                        adapter.notifyDataSetChanged();
                        refreshTotalPrice();
                        showNoItem();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void itemDAOUpdate(Item item) {
        if (itemDAO.update(item))
            Log.i(TAG, "商品"+ item.getName()+"更新成功");
        else
            Log.i(TAG, "商品"+ item.getName()+"更新失敗");
    }

    private void itemDAODelete(Item item) {
        if (itemDAO.delete(item.getId()))
            Log.i(TAG, "商品"+ item.getName()+"刪除成功");
        else
            Log.i(TAG, "商品"+ item.getName()+"刪除失敗");
    }

    // 判斷可否前進下一頁
    // 檢查尺寸是否有選擇
    private boolean goToNextPage() {
        boolean b = true;
        for(int i=0;i<lists.size();i++){
            if(lists.get(i).getPid().indexOf("A") != -1) {
                if(lists.get(i).getSpec().equals("none")) {
                    b = false;
                    break;
                }
            }
        }
        return b;
    }

    //結帳
    private View.OnClickListener submitClkLis = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(goToNextPage()) {
                Intent intent = new Intent(getActivity(), CartReviewActivity.class);
                getActivity().startActivity(intent);
            } else {
                Toast.makeText(getActivity(),"未選擇規格。",Toast.LENGTH_SHORT).show();
            }
        }
    };
}
