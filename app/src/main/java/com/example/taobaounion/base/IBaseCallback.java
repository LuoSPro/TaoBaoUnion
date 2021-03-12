package com.example.taobaounion.base;

import com.example.taobaounion.model.domain.Categories;

public interface IBaseCallback {
    void onError();

    void onLoading();

    void onEmpty();
}
