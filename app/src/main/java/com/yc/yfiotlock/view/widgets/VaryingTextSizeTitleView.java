package com.yc.yfiotlock.view.widgets;

import android.content.Context;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/4/1
 **/
public class VaryingTextSizeTitleView extends ColorTransitionPagerTitleView {
    public VaryingTextSizeTitleView(Context context) {
        super(context);
    }

    int selectTextSize;
    int deSelectTextSize;
    int selectUnit;
    int deSelectUnit;

    public void setSelectTextSize(int selectUnit, int size) {
        this.selectTextSize = size;
        this.selectUnit = selectUnit;
    }

    public void setDeSelectTextSize(int deSelectUnit, int deSelectTextSize) {
        this.deSelectTextSize = deSelectTextSize;
        this.deSelectUnit = deSelectUnit;
    }

    @Override
    public void onSelected(int index, int totalCount) {
        super.onSelected(index, totalCount);
        setTextSize(selectUnit, selectTextSize);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        super.onDeselected(index, totalCount);
        setTextSize(deSelectUnit, deSelectTextSize);
    }
}
