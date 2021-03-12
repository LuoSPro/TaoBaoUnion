package com.example.taobaounion.view;

import com.example.taobaounion.base.IBaseCallback;
import com.example.taobaounion.model.domain.SelectedContent;
import com.example.taobaounion.model.domain.SelectedPageCategory;

public interface ISelectedPageCallback extends IBaseCallback {


    /**
     * 分类内容
     * @param categories 分类内容
     */
    void onCategoriesLoaded(SelectedPageCategory categories);


    /**
     * 内容
     * @param content 内容
     */
    void onContentLoaded(SelectedContent content);
}
