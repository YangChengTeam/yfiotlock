package com.yc.yfiotlock.view.adapters;

import com.baidu.mapapi.search.core.PoiInfo;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.view.BaseExtendAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LocationAdapter extends BaseExtendAdapter<PoiInfo> {

    public LocationAdapter(@Nullable List<PoiInfo> data) {
        super(R.layout.item_location, data);
    }


    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PoiInfo poiInfo) {
        baseViewHolder.setText(R.id.tv_location_name, poiInfo.getName())
                .setText(R.id.tv_location_des, poiInfo.getAddress());

        baseViewHolder.setGone(R.id.view_line_location, baseViewHolder.getLayoutPosition() == 0);
    }
}
