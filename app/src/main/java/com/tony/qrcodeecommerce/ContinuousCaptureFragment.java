package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.tony.qrcodeecommerce.utils.AppSP;
import com.tony.qrcodeecommerce.utils.AsyncImageLoader;
import com.tony.qrcodeecommerce.utils.Item;
import com.tony.qrcodeecommerce.utils.ItemDAO;
import com.tony.qrcodeecommerce.utils.Product;
import com.tony.qrcodeecommerce.utils.ProductDAO;
import com.tony.qrcodeecommerce.utils.Tool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
public class ContinuousCaptureFragment extends Fragment {
    private static final String TAG = "QRCodeEcommerce::CCA";
    private CompoundBarcodeView barcodeView;
    private LinearLayout detailsLL;
    private Tool tool;
    private ItemDAO itemDAO;
    private Item item;

    private float scale;

    // 顯示文字內容
    private String text = "";

    private ProductDAO productDAO;

    private AppSP appSP;

    private AsyncImageLoader asyncImageLoader;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //取得MainActivity的方法，將文字放入text字串
        MainActivity mMainActivity = (MainActivity) activity;
        text = mMainActivity.getQRCodeText();
        appSP = new AppSP(getActivity());
        asyncImageLoader = new AsyncImageLoader(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //導入Tab分頁的Fragment Layout
        return inflater.inflate(R.layout.continuous_scan, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        scale = getResources().getDisplayMetrics().density;

        barcodeView = (CompoundBarcodeView) getActivity().findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
        detailsLL = (LinearLayout) getActivity().findViewById(R.id.detailsLayout);
        tool = new Tool();
        itemDAO = new ItemDAO(getActivity());
        productDAO = new ProductDAO(getActivity());

        List<Product> productList = productDAO.getAll();
        Log.i(TAG,"productList size:"+productList.size());
    }

    public BarcodeCallback callback = new BarcodeCallback() {
        //當辨識到之後要做什麼動作
        @Override
        public void barcodeResult(BarcodeResult result) {
//            if (result.getText() != null) {
//                barcodeView.setStatusText(result.getText());
//            }

            Log.i(TAG,"result:"+result.getText());

            /**
            *  init view
            */

            // inflater 引入layout
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.continous_sublayout,null);
            // 標題
            TextView tv = (TextView) view.findViewById(R.id.title);
            // 商品圖片
            ImageView imageView = (ImageView) view.findViewById(R.id.img);
            // 規格下拉式選單
            Spinner specSP = (Spinner) view.findViewById(R.id.specSP);
            TextView specSP2 = (TextView) view.findViewById(R.id.specSP2);
            // 新增購物車
            ImageView addCart = (ImageView) view.findViewById(R.id.addCart);
            addCart.setOnClickListener(addCartClickLis);

            appSP.setScanPid(result.getText());

            String titleStr = "";       //商品title

            String sql = "SELECT pid,name,price,pic,pic_link,link,spec FROM product "
                    + " WHERE pid = '" + result.getText() + "';";
            Cursor c = productDAO.query(sql);

            Bitmap bitmap;

            if (c.getCount() > 0) {
                ArrayList<String> specArr = new ArrayList<>();
                c.moveToFirst();
                //編號，商品編號，名稱，價錢，圖片名稱，圖片連結，商品連結，加入日期
                item = new Item(0, c.getString(0), c.getString(1), c.getInt(2), c.getString(3),
                        c.getString(4), c.getString(5), new Date().getTime());
                titleStr = c.getString(1);
                specArr.add(c.getString(6));
                imageView.setTag(c.getString(4));
                bitmap = asyncImageLoader.loadImage(imageView, c.getString(4));
                while(c.moveToNext()) {
                    specArr.add(c.getString(6));
                }
                //尺寸規格 (Spinner)
                if(item.getPid().indexOf("A") != -1) { //如果是衣服
                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, specArr);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    specSP.setAdapter(spinner_adapter);
                    specSP.setVisibility(View.VISIBLE);
                    specSP2.setVisibility(View.VISIBLE);
                    specSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            //取得目前選擇的尺寸
                            String choiceSize = parent.getSelectedItem().toString();
                            item.setSpec(choiceSize);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                } else { //如果不是衣服
                    specSP.setVisibility(View.GONE);
                    specSP2.setVisibility(View.GONE);
                }

                if( ((MainActivity)getActivity()).getDetailsFragment()!=null ){
                    ((MainActivity)getActivity()).getDetailsFragment().ChangeData();
                }
            } else {
                item = null;
                titleStr = "找不到商品";
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_not_found);
                specSP.setVisibility(View.GONE);
                specSP2.setVisibility(View.GONE);
                addCart.setVisibility(View.GONE);
            }

            //設定商品標題
            tv.setText(titleStr);
            //設定商品圖片
            imageView.setImageBitmap(bitmap);
            // 新增
            detailsLL.removeAllViews();
            detailsLL.addView(view);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private View.OnClickListener addCartClickLis = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            addCartDialog();
        }
    };

    private void addCartDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示訊息")
                .setMessage("要將商品新增至購物車嗎?")
                .setPositiveButton("新增", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (item != null) {
                            if (!itemDAO.checkPidAndSpec(item)) { //如果裡面沒有同樣的pid
                                if (item.getPid().indexOf("A") != -1) { //如果是衣服
                                    /**
                                                                         * 判斷是否有選擇尺寸
                                                                         **/
                                    //取得第一個選項
                                    String sizeArr01 = getResources().getStringArray(R.array.cart_size_spinner)[0];
                                    //如果有選擇尺寸，就加入購物車
                                    if(!item.getSpec().equals("none") && !item.getSpec().equals(sizeArr01)) {
                                        submitAddCart();
                                    } else { //如果沒選擇尺寸，就顯示提示訊息
                                        Toast.makeText(getActivity(), "尚未選擇尺寸！", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    submitAddCart();
                                }
                            } else {
                                Toast.makeText(getActivity(), "已有相同商品！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "無法加入購物車！", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void submitAddCart() {
        Item item1 = itemDAO.insert(item);
        Log.i(TAG, "item name:"+item1.getName());
        Log.i(TAG, "item number:"+item1.getNumber());
        Toast.makeText(getActivity(), "成功加入購物車！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    //暫停
    public void pause(View view) {
        barcodeView.pause();
    }

    //恢復
    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.getActivity().onKeyDown(keyCode, event);
    }
    */
}
