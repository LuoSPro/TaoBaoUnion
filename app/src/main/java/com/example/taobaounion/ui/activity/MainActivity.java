package com.example.taobaounion.ui.activity;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseActivity;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.ui.fragment.HomeFragment;
import com.example.taobaounion.ui.fragment.SelectedFragment;
import com.example.taobaounion.ui.fragment.OnSellFragment;
import com.example.taobaounion.ui.fragment.SearchFragment;
import com.example.taobaounion.utils.LogUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements IMainActivity{

    @BindView(R.id.main_navigation_bar)
    public BottomNavigationView mNavigationView;
    private SearchFragment mSearchFragment;
    private SelectedFragment mRecommendFragment;
    private OnSellFragment mRedPacketFragment;
    private HomeFragment mHomeFragment;
    private FragmentManager mFm;

    @Override
    protected void initPresenter() {

    }

    @Override
    protected void initEvent() {
        initListener();
    }

    @Override
    protected void initView() {
        initFragments();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }


    private void initFragments() {
        mHomeFragment = new HomeFragment();
        mRedPacketFragment = new OnSellFragment();
        mRecommendFragment = new SelectedFragment();
        mSearchFragment = new SearchFragment();
        mFm = getSupportFragmentManager();
        switchFragment(mHomeFragment);//默认首页
    }

    private void initListener() {
         mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                 Log.d(TAG, "title ==> " + item.getTitle() + "id ==> " + item.getItemId());
                 if (item.getItemId() == R.id.home){
                     LogUtils.d(this,"主页");
                     switchFragment(mHomeFragment);
                 }else if (item.getItemId() == R.id.search){
                     LogUtils.d(this,"搜索");
                     switchFragment(mSearchFragment);
                 }else if (item.getItemId() == R.id.red_packet){
                     LogUtils.d(this,"红包");
                     switchFragment(mRedPacketFragment);
                 }else if (item.getItemId() == R.id.selected){
                     LogUtils.d(this,"精选");
                     switchFragment(mRecommendFragment);
                 }
                 return true;
             }
         });
    }

    /**
     * 上一次显示的fragment
     */
    private BaseFragment lastOneFragment = null;

    private void switchFragment(BaseFragment targetFragment) {
        //如果上次的fragment和跳转的fragment一样，那么就不进行跳转
        if (targetFragment == lastOneFragment){
            return;
        }
        //修改成add和hide的方式控制Fragment的切换
        FragmentTransaction transaction = mFm.beginTransaction();
        if (!targetFragment.isAdded()){
            transaction.add(R.id.main_page_container,targetFragment);
        }else {
            transaction.show(targetFragment);
        }
        if (lastOneFragment != null){//讲上一次使用的fragment隐藏
            transaction.hide(lastOneFragment);
        }
        lastOneFragment = targetFragment;

        //transaction.replace(R.id.main_page_container,targetFragment);//替换
        transaction.commit();//记得提交事务
    }

    /**
     * 切换到搜索页面
     */
    @Override
    public void switch2Search() {
        //switchFragment(mSearchFragment);
        //切换导航栏的选中项
        mNavigationView.setSelectedItemId(R.id.search);
    }

//    private void initView() {
//        HomeFragment homeFragment = new HomeFragment();
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction transaction = fm.beginTransaction();
//        transaction.add(R.id.main_page_container,homeFragment);
//        transaction.commit();
//    }
}
