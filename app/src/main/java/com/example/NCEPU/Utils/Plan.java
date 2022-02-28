package com.example.NCEPU.Utils;


import java.util.List;

public class Plan {

    private String minCredit;
    private String currentCredit;
    private String tag;  //表明是必修还是实践还是专选
    private List<SubPlan> plans;

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    public void setPlans(List<SubPlan> plans) {
        this.plans = plans;
    }

    public List<SubPlan> getPlans() {
        return this.plans;
    }

    public void setMinCredit(String minCredit) {
        this.minCredit = minCredit;
    }

    public String getMinCredit() {
        return this.minCredit;
    }

    public void setCurrentCredit(String currentCredit) {
        this.currentCredit = currentCredit;
    }

    public String getCurrentCredit() {
        return this.currentCredit;
    }
}

