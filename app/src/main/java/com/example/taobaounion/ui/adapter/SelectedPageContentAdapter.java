package com.example.taobaounion.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.model.domain.IBaseInfo;
import com.example.taobaounion.model.domain.SelectedContent;
import com.example.taobaounion.utils.Constants;
import com.example.taobaounion.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedPageContentAdapter extends RecyclerView.Adapter<SelectedPageContentAdapter.InnerHolder> {
    private List<SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean > mData
            = new ArrayList<>();
    private OnSelectPageContentItemClickListener mContentItemListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_content, parent, false);
            return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean  itemData = mData.get(position);
        holder.setData(itemData);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContentItemListener != null) {
                    mContentItemListener.onContentItemClick(itemData);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(SelectedContent content) {
        if (content.getCode() == Constants.SUCCESS_CODE){
            List<SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean > uatm_tbk_item
                    = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data();
            this.mData.clear();
            this.mData.addAll(uatm_tbk_item);
            notifyDataSetChanged();
        }
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.selected_cover)
        public ImageView cover;

        @BindView(R.id.selected_off_price)
        public TextView offPriceTv;

        @BindView(R.id.selected_title)
        public TextView title;

        @BindView(R.id.selected_buy_btn)
        public TextView buyBtn;

        @BindView(R.id.selected_original_price)
        public TextView originalPriceTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setData(SelectedContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean  itemData) {
            title.setText(itemData.getTitle());
            String pict_url = itemData.getPict_url();
            LogUtils.d(this,"pic url ==> " + pict_url);
            Glide.with(itemView.getContext()).load(pict_url).into(cover);
            //判断是否有优惠券来处理逻辑
            if (TextUtils.isEmpty(itemData.getCoupon_click_url())) {
                originalPriceTv.setText("晚啦，没有优惠券了");
                buyBtn.setVisibility(View.GONE);
            }else {
                originalPriceTv.setText("原价：" + itemData.getZk_final_price());
                buyBtn.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(itemData.getCoupon_info())){
                offPriceTv.setVisibility(View.GONE);
            }else{
                offPriceTv.setVisibility(View.VISIBLE);
                offPriceTv.setText(itemData.getCoupon_info());

            }
        }
    }

    public void setOnSelectPageContentItemClickListener(OnSelectPageContentItemClickListener listener){
        this.mContentItemListener = listener;
    }

    public interface OnSelectPageContentItemClickListener{
        void onContentItemClick(IBaseInfo item);
    }
}
