package com.yc.yfiotlock.view.adapters;

import android.view.View;

import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yc.yfiotlock.R;
import com.yc.yfiotlock.model.bean.lock.ble.OpenLockInfo;
import com.yc.yfiotlock.model.bean.lock.remote.PasswordInfo;
import com.yc.yfiotlock.view.BaseExtendAdapter;
import com.yc.yfiotlock.view.widgets.NoDataView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TempPwdAdapter extends BaseExtendAdapter<OpenLockInfo> implements LoadMoreModule {
    private long synctime;
    private int LIMIT = 5;

    public TempPwdAdapter() {
        super(R.layout.item_temp_pwd, null);
    }

    public void setSynctime(long synctime) {
        this.synctime = synctime;
    }

    private long getTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setTimeInMillis(time);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        cal.set(year, month, day, hour, minute, 0);
        return cal.getTimeInMillis();
    }

    @Override
    public void setNewInstance(@Nullable List<OpenLockInfo> list) {
        List lastlist = new ArrayList();
        for(OpenLockInfo openLockInfo : list){
            long millis = synctime - openLockInfo.getAddtime();
            int mins = (int) ((millis / (1000 * 60)));
            if (mins <= LIMIT) {
                lastlist.add(openLockInfo);
            }
        }
        if(lastlist.size() == 0){
            setEmptyView(new NoDataView(getContext()));
        }
        super.setNewInstance(lastlist);
    }

    private String format(long time) {
        if(time == 0) return "-";

        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setTimeInMillis(time + LIMIT*1000*60);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        return dateFormat.format(cal.getTime());
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, OpenLockInfo openLockInfo) {
        baseViewHolder.setText(R.id.tv_temp_pwd_name, openLockInfo.getPassword())
                .setText(R.id.tv_temp_pwd_validity, "过期时间：" + format(openLockInfo.getAddtime()));

        int textColor = getContext().getResources().getColor(R.color.blue_2F90F7);
        baseViewHolder.setText(R.id.tv_temp_pwd_state, "有效")
                .setTextColor(R.id.tv_temp_pwd_state, textColor);
        
        baseViewHolder.setGone(R.id.view_line, baseViewHolder.getAdapterPosition() == 0);
    }
}
