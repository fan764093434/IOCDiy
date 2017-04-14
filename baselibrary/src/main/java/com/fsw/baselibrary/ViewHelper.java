package com.fsw.baselibrary;

import android.app.Activity;
import android.view.View;

/**
 * @author fsw
 * @version 1.0
 * @teacher Darren
 * @time 2017/4/13
 * @desc View的findViewById的辅助类
 */

public class ViewHelper {

    private View view;

    private Activity activity;

    public ViewHelper(View view) {
        this.view = view;
    }

    public ViewHelper(Activity activity) {
        this.activity = activity;
    }

    public View findViewById(int id) {
        return activity != null ? activity.findViewById(id) : view.findViewById(id);
    }
}
