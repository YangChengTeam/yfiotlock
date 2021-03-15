package com.yc.yfiotlock.model.engin;

/**
 * @author Dullyoung
 * Created by　Dullyoung on 2021/3/15
 **/
public class LoginEvent {

    public enum State {
        /**
         * <summary>CHECKING 正在检测是否支持一键登录</summary>
         */
        CHECKING("checking"),
        /**
         * <summary>WAITING 等待唤起</summary>
         */
        WAITING("waiting"),
        /**
         * <summary>FAILED 失败情况</summary>
         */
        FAILED("failed"),
        /**
         * <summary>EVOKE_SUCCESS 唤起成功</summary>
         */
        EVOKE_SUCCESS("success");

        private final String value;

        State(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private State stateString;
    private String code;

    public State getStateString() {
        return stateString;
    }

    public void setStateString(State state) {
        this.stateString = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
                "stateString=" + stateString +
                ", code='" + code + '\'' +
                '}';
    }
}
