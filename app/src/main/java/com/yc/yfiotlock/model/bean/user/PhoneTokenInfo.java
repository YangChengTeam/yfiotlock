package com.yc.yfiotlock.model.bean.user;

/*
 * Created by　Dullyoung on 2021/3/5
 */
public class PhoneTokenInfo {

    private String carrierFailedResultData;
    private String code;
    private String msg;
    private String token;
    private int requestCode;
    private String requestId;
    private String vendorName;

    public String getCarrierFailedResultData() {
        return carrierFailedResultData;
    }

    public void setCarrierFailedResultData(String carrierFailedResultData) {
        this.carrierFailedResultData = carrierFailedResultData;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
}
