package com.example.taobaounion.base;

import android.app.Activity;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vondear.rxfeature.activity.ActivityScanerCode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());


        //实现清明节灰的效果
        //====================================
        //ColorMatrix cm = new ColorMatrix();
        //cm.setSaturation(0);
        //Paint paint = new Paint();
        //paint.setColorFilter(new ColorMatrixColorFilter(cm));
        ////这样改变不了状态栏，所以应该去获取整个Window
        ////View contentContainer = findViewById(android.R.id.content);
        //View contentContainer = getWindow().getDecorView();
        //contentContainer.setLayerType(View.LAYER_TYPE_SOFTWARE,paint);
        //==========================================

        mBind = ButterKnife.bind(this);
        initView();
        initEvent();
        initPresenter();
    }

    protected abstract void initPresenter();

    /**
     * 需要的时候覆写
     */
    protected void initEvent() {
    }

    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBind == null) {
            mBind.unbind();
        }
        this.release();
    }

    /**
     * 子类需要时，覆盖即可
     */
    protected void release() {

    }

    protected abstract int getLayoutResId();

}
