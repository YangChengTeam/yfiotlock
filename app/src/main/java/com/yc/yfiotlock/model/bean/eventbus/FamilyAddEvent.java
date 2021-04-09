package com.yc.yfiotlock.model.bean.eventbus;

import com.yc.yfiotlock.model.bean.lock.FamilyInfo;

public class FamilyAddEvent {
    private FamilyInfo familyInfo;
    public FamilyAddEvent(FamilyInfo familyInfo){
        this.familyInfo = familyInfo;
    }

    public FamilyInfo getFamilyInfo() {
        return familyInfo;
    }
}
