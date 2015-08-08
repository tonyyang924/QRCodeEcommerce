package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private MainApplication m;

    private float scale;

    //顯示文字內容
    private String text = "";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        m = (MainApplication) getActivity().getApplication();
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

            m.setPid(result.getText());

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

            Bitmap bitmap = BitmapFactory.decodeFile(image_path);
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageBitmap(bitmap);
            int sizew = (int) (getActivity().getResources().getDimension(R.dimen.cca_img_size) * scale);
            int sizeh = (int) (getActivity().getResources().getDimension(R.dimen.cca_img_size) * scale);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(sizew, sizeh));
            imageView.setOnClickListener(addCartClickLis);

            TextView tv = new TextView(getActivity());
            tv.setText(str);
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setPadding(20, 20, 20, 20);
            tv.setBackgroundColor(Color.RED);
            tv.setTextSize(20);
            tv.setTextColor(Color.WHITE);

            int size = (int) (getActivity().getResources().getDimension(R.dimen.cca_icon_size) * scale);
            ImageView addCart = new ImageView(getActivity());
            addCart.setImageResource(R.drawable.addtocartlight);
            addCart.setOnClickListener(addCartClickLis);
            addCart.setLayoutParams(new RelativeLayout.LayoutParams(size, size));

            /**
             * 下半部
             */
            LinearLayout ll_ho = new LinearLayout(getActivity());
            ll_ho.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            ll_ho.setOrientation(LinearLayout.HORIZONTAL);

            //左邊圖片
            ll_ho.addView(imageView);

            //讓add cart置中
            RelativeLayout rl = new RelativeLayout(getActivity());
            rl.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            RelativeLayout.LayoutParams addcart_layoutParams =
                    (RelativeLayout.LayoutParams) addCart.getLayoutParams();
            addcart_layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            addCart.setLayoutParams(addcart_layoutParams);

            rl.addView(addCart);
            ll_ho.addView(rl);

            detailsLL.removeAllViews();
            detailsLL.addView(tv);
            detailsLL.addView(ll_ho);
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
                                Item item1 = itemDAO.insert(item);
                                Log.i(TAG, item1.getName());
                                Toast.makeText(getActivity(), "成功加入購物車！", Toast.LENGTH_SHORT).show();
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
