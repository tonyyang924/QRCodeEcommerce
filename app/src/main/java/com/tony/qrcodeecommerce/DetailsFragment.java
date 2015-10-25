package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tony.qrcodeecommerce.utils.AppSP;
import com.tony.qrcodeecommerce.utils.AsyncImageLoader;
import com.tony.qrcodeecommerce.utils.ProductDAO;

public class DetailsFragment extends Fragment {
    private static final String TAG = "QRCodeEcommerce::DetailsFragment";
    private TextView nameTV, priceTV ,desTV;
    private ImageView imgIV;
    private ProductDAO productDAO;
    private AsyncImageLoader asyncImageLoader;
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
        asyncImageLoader = new AsyncImageLoader(getActivity());
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
        priceTV = (TextView) getActivity().findViewById(R.id.priceTV);
        desTV = (TextView) getActivity().findViewById(R.id.descriptorsTV);
        // 目前是demo用的資料
        desTV.setText(Html.fromHtml("<font color=\"#FF0066\">【圖案特色】</font> <br/>第一科技大學經典版T恤；運用第一科技大學校名縮寫做設計，送禮自用兩相宜。<br/>" +
                "<font color=\"#FF0066\">【材質】<br/></font> 25支棉(精梳棉)，採用台灣製預縮棉、保證不縮水；觸感舒適、吸汗、無靜電。<br/>" +
                "<font color=\"#FF0066\">【印製】<br/></font> 採網版印刷，圖案不黏不裂，質感好。<br/>" +
                "<font color=\"#FF0066\">【款式】</font> 直肩、直筒"));
        imgIV = (ImageView) getActivity().findViewById(R.id.imgIV);
        noProductLayout = (RelativeLayout) getActivity().findViewById(R.id.noProductLayout);
        productDAO = new ProductDAO(getActivity());
        ChangeData();
    }
    public void ChangeData() {
        Cursor c = productDAO.query("SELECT name,pic_link,price FROM product "
                + " WHERE pid = '" + appSP.getScanPid() + "';");
        if (c.getCount() > 0) {
            c.moveToFirst();
            String name = c.getString(0);
            int price = c.getInt(2);
            nameTV.setText(name);
//            priceTV.setText("價格：" + price);
            priceTV.setText(Html.fromHtml("<font color=\"#FF0066\">【價格】</font> " + price));
            imgIV.setTag(c.getString(1));
            Bitmap bitmap = asyncImageLoader.loadImage(imgIV,c.getString(1));
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
