package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tony.qrcodeecommerce.utils.AppSP;
import com.tony.qrcodeecommerce.utils.Tool;

public class DetailsFragment extends Fragment {
    private static final String TAG = "QRCodeEcommerce::DetailsFragment";
    private TextView nameTV, priceTV;
    private ImageView imgIV;
    private Tool tool;
    private RelativeLayout noProductLayout;
    //顯示文字內容
    private String text = "";
    private AppSP appSP;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //取得MainActivity的方法，將文字放入text字串
        MainActivity mMainActivity = (MainActivity) activity;
        text = mMainActivity.getDetailsText();
        appSP = new AppSP(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //導入Tab分頁的Fragment Layout
        return inflater.inflate(R.layout.activity_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        nameTV = (TextView) getActivity().findViewById(R.id.nameTV);
        priceTV = (TextView) getActivity().findViewById(R.id.subTotalTV);
        imgIV = (ImageView) getActivity().findViewById(R.id.imgIV);
        noProductLayout = (RelativeLayout) getActivity().findViewById(R.id.noProductLayout);
        tool = new Tool();
        Cursor c = tool.SQLQuery("SELECT name,pic,price FROM item "
                + " WHERE id = '" + appSP.getScanPid() + "';");
        if (c.getCount() > 0) {
            c.moveToFirst();
            String name = c.getString(0);
            int price = c.getInt(2);
            nameTV.setText(name);
            priceTV.setText("價格：" + price);
            Bitmap bitmap = BitmapFactory.decodeFile(tool.QRCodeEcommercePath + "/images/" + c.getString(1));
            imgIV.setImageBitmap(bitmap);
            nameTV.setVisibility(View.VISIBLE);
            priceTV.setVisibility(View.VISIBLE);
            imgIV.setVisibility(View.VISIBLE);
            noProductLayout.setVisibility(View.GONE);
        } else {
            nameTV.setVisibility(View.INVISIBLE);
            priceTV.setVisibility(View.INVISIBLE);
            imgIV.setVisibility(View.INVISIBLE);
            noProductLayout.setVisibility(View.VISIBLE);
        }
    }

}
