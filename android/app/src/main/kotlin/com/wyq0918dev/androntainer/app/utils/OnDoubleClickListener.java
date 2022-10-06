package com.wyq0918dev.androntainer.app.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.VibrateUtils;

public class OnDoubleClickListener implements View.OnTouchListener {

    private final Handler handler;
    private final MyClickCallBack myClickCallBack;
    private int clickCount = 0;

    public OnDoubleClickListener(MyClickCallBack myClickCallBack) {
        this.myClickCallBack = myClickCallBack;
        handler = new Handler();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            clickCount++;
            int timeout = 400;
            long vibrate = 20;
            VibrateUtils.vibrate(vibrate);
            handler.postDelayed(() -> {
                if (clickCount == 1){
                    myClickCallBack.onClick();
                } else if (clickCount >= 2){
                    myClickCallBack.OnDoubleClick();
                }
                handler.removeCallbacksAndMessages(null);
                clickCount = 0;
            }, timeout);
        }
        return false;
    }

    public interface MyClickCallBack {
        void onClick();

        void OnDoubleClick();
    }
}
