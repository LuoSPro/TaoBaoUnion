package com.example.taobaounion.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.taobaounion.R;
import com.example.taobaounion.ui.custom.TextFlowLayout;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.ToastUtil;
import com.google.android.material.internal.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends Activity {

    @BindView(R.id.test_navigation_bar)
    public RadioGroup navigationBar;

    @BindView(R.id.test_flow_text)
    public TextFlowLayout flowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        initListener();

        List<String> testList = new ArrayList<>();
        testList.add("电脑");
        testList.add("机械键盘");
        testList.add("憨包");
        testList.add("运行鞋");
        testList.add("肥仔快乐水");
        testList.add("白开水");
        testList.add("Android进阶之光");
        testList.add("Android群英传");
        testList.add("Android开发艺术探索");
        flowText.setTextList(testList);
        flowText.setOnFlowTextItemClickListener(new TextFlowLayout.OnFlowTextItemClickListener() {
            @Override
            public void onFlowItemClick(String text) {
                LogUtils.d(TestActivity.this,"click text ==> " + text);
                ToastUtil.showToast(text);
            }
        });

    }

    public void showToast(View view){
        ToastUtil.showToast("测试。。。");
    }

    private void initListener() {
        navigationBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.test_home:
                        LogUtils.d(TestActivity.class,"首页");
                        break;
                    case R.id.test_redPacket:
                        LogUtils.d(TestActivity.class,"红包");
                        break;
                    case R.id.test_selected:
                        LogUtils.d(TestActivity.class,"精选");
                        break;
                    case R.id.test_search:
                        LogUtils.d(TestActivity.class,"查找");
                        break;
                }
            }
        });
    }
}
