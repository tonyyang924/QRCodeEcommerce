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
import com.tony.qrcodeecommerce.utils.Item;
import com.tony.qrcodeecommerce.utils.ItemDAO;
import com.tony.qrcodeecommerce.utils.Tool;

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

    //顯示文字內容
    private String text = "";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //取得MainActivity的方法，將文字放入text字串
        MainActivity mMainActivity = (MainActivity) activity;
        text = mMainActivity.getQRCodeText();
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
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        //當辨識到之後要做什麼動作
        @Override
        public void barcodeResult(BarcodeResult result) {
//            if (result.getText() != null) {
//                barcodeView.setStatusText(result.getText());
//            }

            MainApplication.setPid(result.getText());

            String sql = "SELECT id,name,price,pic,pic_link,link FROM item "
                    + " WHERE id = '" + result.getText() + "';";
            Cursor c = tool.SQLQuery(sql);
            String str = "";
            String image_path = "";

            if (c.getCount() > 0) {
                c.moveToFirst();
                item = new Item(0, c.getString(0), c.getString(1), c.getInt(2), c.getString(3),
                        c.getString(4), c.getString(5), new Date().getTime());
                str = c.getString(1);
                image_path = Tool.QRCodeEcommercePath + "/images/" + c.getString(3);
            } else {
                item = null;
                str = "找不到商品";
                image_path = Tool.QRCodeEcommercePath + "/images/image-not-found.jpg";
            }

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.continous_sublayout,null);

            //商品圖片
            Bitmap bitmap = BitmapFactory.decodeFile(image_path);
            ImageView imageView = (ImageView) view.findViewById(R.id.img);
            imageView.setImageBitmap(bitmap);
//            imageView.setOnClickListener(addCartClickLis);

            //商品名稱標題
            TextView tv = (TextView) view.findViewById(R.id.title);
            tv.setText(str);

            //尺寸規格 (Spinner)
            Spinner specSP = (Spinner) view.findViewById(R.id.specSP);
            if(item.getPid().indexOf("A") != -1) { //如果是衣服
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(getActivity(),
                        R.array.cart_size_spinner, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                specSP.setAdapter(spinner_adapter);
                specSP.setVisibility(View.VISIBLE);
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
            } else {
                specSP.setVisibility(View.GONE);
            }

            // 新增購物車
            ImageView addCart = (ImageView) view.findViewById(R.id.addCart);
            addCart.setOnClickListener(addCartClickLis);

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
                            if (!itemDAO.checkPid(item)) { //如果裡面沒有同樣的pid
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
        Log.i(TAG, item1.getName());
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
