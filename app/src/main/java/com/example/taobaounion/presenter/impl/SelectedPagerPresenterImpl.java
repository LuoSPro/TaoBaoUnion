package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.SelectedContent;
import com.example.taobaounion.model.domain.SelectedPageCategory;
import com.example.taobaounion.presenter.ISelectPresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.ISelectedPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SelectedPagerPresenterImpl implements ISelectPresenter {

    private ISelectedPageCallback mViewCallback = null;
    private final Api mApi;
//    private SelectedPageCategory.DataBean mCurrentCategoryItem = null;

    public SelectedPagerPresenterImpl() {
        //拿到retrofit
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    @Override
    public void getCategories() {
        //加载放在这里的原因是，因为先加载目录，再加载内容
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }
        Call<SelectedPageCategory> task = mApi.getSelectedPageCategories();
        task.enqueue(new Callback<SelectedPageCategory>() {
            @Override
            public void onResponse(Call<SelectedPageCategory> call, Response<SelectedPageCategory> response) {
                int code = response.code();
                LogUtils.d(this,"code ==》 " + code);
                if (code == HttpURLConnection.HTTP_OK){
                    SelectedPageCategory result = response.body();
                    //通知UI更新
                    if (mViewCallback != null) {
                        mViewCallback.onCategoriesLoaded(result);
                    }
                }else {
                    onLoadedError();
                }
            }

            @Override
            public void onFailure(Call<SelectedPageCategory> call, Throwable t) {
                onLoadedError();
            }
        });
    }

    private void onLoadedError() {
        if (mViewCallback != null) {
            mViewCallback.onError();
        }
    }

    @Override
    public void getContentCategory(SelectedPageCategory.DataBean item) {
//        this.mCurrentCategoryItem = item;
        LogUtils.d(this,"item.getFavorites_id() ==> " + item.getFavorites_id());
        String targetUrl = UrlUtils.getSelectedPageContentUrl(item.getFavorites_id());
        Call<SelectedContent> task = mApi.getSelectedContent(targetUrl);
        task.enqueue(new Callback<SelectedContent>() {
            @Override
            public void onResponse(Call<SelectedContent> call, Response<SelectedContent> response) {
                int code = response.code();
                LogUtils.d(SelectedPagerPresenterImpl.this,"code ==》 " + code);
                if (code == HttpURLConnection.HTTP_OK){
                    SelectedContent result = response.body();
                    //通知UI更新
                    if (mViewCallback != null) {
                        mViewCallback.onContentLoaded(result);
                    }
                }else {
                    onLoadedError();
                }
            }

            @Override
            public void onFailure(Call<SelectedContent> call, Throwable t) {
                onLoadedError();
            }
        });
    }

    @Override
    public void reloadContent() {
        //注：这里不应该是加载内容，而是加载分类，如果断网时重新加载时，左侧没有目录。mCurrentCategoryItem始终未null
        //所以这里应该是重新加载目录
//        if (mCurrentCategoryItem != null) {
//            this.getContentCategory(mCurrentCategoryItem);
//        }
        this.getCategories();
    }

    @Override
    public void registerViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback = null;
    }
}
