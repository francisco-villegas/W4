package com.example.francisco.w4;

/**
 * Created by FRANCISCO on 24/08/2017.
 */

public interface BasePresenter <V extends BaseView>{

    void attachView(V view);
    void detachView();
}
