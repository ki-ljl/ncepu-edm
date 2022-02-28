/**
 * Date   : 2021/2/25 10:10
 * Author : KI
 * File   : Apriori
 * Desc   : Apriori
 * Motto  : Hungry And Humble
 */
package com.example.NCEPU.Utils;

import java.util.ArrayList;

public class Apriori {
    ArrayList<String> antecedents;
    ArrayList<String> consequents;
    String conf;
    ArrayList<String> preCourses;
    ArrayList<String> backCourses;
    String state;  //待验证、已验证
    String suggestion;

    public void setAntecedents(ArrayList<String> antecedents) {
        this.antecedents = antecedents;
    }

    public ArrayList<String> getAntecedents() {
        return antecedents;
    }

    public void setConsequents(ArrayList<String> consequents) {
        this.consequents = consequents;
    }

    public ArrayList<String> getConsequents() {
        return consequents;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public String getConf() {
        return conf;
    }

    public void setPreCourses(ArrayList<String> preCourses) {
        this.preCourses = preCourses;
    }

    public ArrayList<String> getPreCourses() {
        return preCourses;
    }

    public void setBackCourses(ArrayList<String> backCourses) {
        this.backCourses = backCourses;
    }

    public ArrayList<String> getBackCourses() {
        return backCourses;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getSuggestion() {
        return suggestion;
    }
}
