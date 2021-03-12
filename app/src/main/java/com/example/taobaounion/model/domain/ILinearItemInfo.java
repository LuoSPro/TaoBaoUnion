package com.example.taobaounion.model.domain;

public interface ILinearItemInfo extends IBaseInfo {
    /**
     * 获取原价
     * @return
     */
    String getFinalPrice();

    /**
     * 获取优惠价格
     * @return
     */
    long getCouponAmount();

    /**
     * 获取销量
     *
     * @return
     */
    long getVolume();
}
