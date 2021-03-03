package com.kk.securityhttp.domain;


import java.util.List;

/**
 * Created by zhangkai on 16/9/20.
 */
public class ChannelInfo {
    public String author;
    public String from_id;
    public String tui_url;
    public boolean is_show_ad = true;
    public boolean is_show_privacy = false;
    public String agent_id = "default";
    public List<TuiInfo> tuiInfos;
    public TuiInfo tuiInfo;
}
