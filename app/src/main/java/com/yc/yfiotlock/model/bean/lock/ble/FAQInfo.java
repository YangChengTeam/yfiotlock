package com.yc.yfiotlock.model.bean.lock.ble;

import java.io.Serializable;

/*
 * Created by　Dullyoung on 2021/3/6
 */
public class FAQInfo implements Serializable {
    private String id;
    private String question;
    private String answer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
