package com.seucpss.contact_detection;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by chen.yingjie on 2019/3/23
 */
public class PopupU {

    /**
     *
     * @param context
     * @param mType

     * @param city
     * @param area
     * @param street
     * @return
     */
    public static Dialog showRegionView(Context context, int mType,  final String city, final String area, final String street, final String village, final OnRegionListener onRegionListener) {
        final Dialog dialog = new Dialog(context, R.style.DialogCommonStyle);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setContentView(R.layout.layout_region);
        RegionPopupWindow regionPopupWindow = window.findViewById(R.id.regionPpw);
        // 设置历史记录
        regionPopupWindow.setHistory(mType, city, area, street, village);
        // 设置右上角叉号监听
        regionPopupWindow.setOnForkClickListener(new RegionPopupWindow.OnForkClickListener() {
            @Override
            public void onForkClick() {
                dialog.dismiss();
            }
        });
        // 设置item监听，回调传回结果
        regionPopupWindow.setOnRpwItemClickListener(new RegionPopupWindow.OnRpwItemClickListener() {
            @Override
            public void onRpwItemClick( String selectedCity, String selectedArea, String selectedStreet, String selectedVillage) {
                onRegionListener.onRegionListener( selectedCity, selectedArea, selectedStreet, selectedVillage);
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = PhoneU.getScreenPix(context).widthPixels;// 宽为手机屏幕宽
        attributes.height = PhoneU.getScreenPix(context).heightPixels * 4/5;// 高为手机屏幕高的4/5
        window.setBackgroundDrawableResource(R.color.white);
        window.setAttributes(attributes);
        window.setWindowAnimations(R.style.AnimBottom);
        dialog.show();
        return dialog;
    }

    public interface OnRegionListener {
        void onRegionListener( String city, String area, String street, String village);
    }
}
