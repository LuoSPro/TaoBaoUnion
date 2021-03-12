package com.example.taobaounion.presenter.impl;

import com.example.taobaounion.model.Api;
import com.example.taobaounion.model.domain.OnSellContent;
import com.example.taobaounion.presenter.IOnSellPagePresenter;
import com.example.taobaounion.utils.LogUtils;
import com.example.taobaounion.utils.RetrofitManager;
import com.example.taobaounion.utils.UrlUtils;
import com.example.taobaounion.view.IOnSellPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnSellPagePresenterImpl implements IOnSellPagePresenter {
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    private IOnSellPageCallback mOnSellPageCallback = null;
    private final Api mApi;

    public OnSellPagePresenterImpl() {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    @Override
    public void getOnSellContent() {
        if (mIsLoading){
            return;
        }
        mIsLoading = true;
        //通知UI状态为加载中
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onLoading();
        }
        //获取特惠内容
        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);
        Call<OnSellContent> task = mApi.getOnSellPageContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                mIsLoading = false;
                int code = response.code();
                LogUtils.d(OnSellPagePresenterImpl.this,"code ==> " + code);
                if (code == HttpURLConnection.HTTP_OK){
                    OnSellContent result = response.body();
                    onSuccess(result);
                }else{
                    onError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                onError();
            }
        });
    }

    private void onSuccess(OnSellContent result) {
        if (mOnSellPageCallback != null) {
            try {
                if (isEmpty(result)){
                    onEmpty();
                }else{
                    mOnSellPageCallback.onContentLoadedSuccess(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onEmpty();
            }
        }
    }

    private boolean isEmpty(OnSellContent content){
        int size = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        return size == 0;
    }

    private void onEmpty(){
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onEmpty();
        }
    }

    private void onError() {
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onError();
        }
    }

    @Override
    public void reload() {
        //重新加载
        this.getOnSellContent();
    }

    /**
     * 当前加载的状态
     * 去控制加载页面的显示和隐藏
     *
     * 防御代码——方式在加载的过程中重复加载
     */
    private boolean mIsLoading = false;

    @Override
    public void loadMore() {
        if (mIsLoading){
            return;
        }
        mIsLoading = true;
        //加载更多
        mCurrentPage++;
        //去加载更多内容
        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);
        Call<OnSellContent> task = mApi.getOnSellPageContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                //结果回来的时候，加载关闭
                mIsLoading = false;
                int code = response.code();
                LogUtils.d(OnSellPagePresenterImpl.this,"code ==> " + code);
                if (code == HttpURLConnection.HTTP_OK){
                    OnSellContent result = response.body();
                    onMoreLoaded(result);
                }else{
                    onMoreLoadedError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable t) {
                onMoreLoadedError();
            }
        });
    }

    private void onMoreLoadedError() {
        mIsLoading = false;
        //当加载更多失败后，页码需要减一下(这是一个细节)
        mCurrentPage--;
        mOnSellPageCallback.onMoreLoadedError();
    }

    private void onMoreLoaded(OnSellContent result) {
        if (mOnSellPageCallback != null) {
            if (!isEmpty(result)){//这里应该是!空的时候
                mOnSellPageCallback.onMoreLoaded(result);
            }else{
                mCurrentPage--;
                mOnSellPageCallback.onMoreLoadedEmpty();
            }
        }
    }

    @Override
    public void registerViewCallback(IOnSellPageCallback callback) {
        this.mOnSellPageCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IOnSellPageCallback callback) {
        //释放引用，避免造成内存泄漏
        this.mOnSellPageCallback = null;
    }
}
