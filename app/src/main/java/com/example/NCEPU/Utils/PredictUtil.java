/**
 * Date   : 2021/2/25 21:14
 * Author : KI
 * File   : PredictUtil
 * Desc   : predict
 * Motto  : Hungry And Humble
 */
package com.example.NCEPU.Utils;

import java.util.ArrayList;

public class PredictUtil {
    String id;
    ArrayList<String> pre_item;
    ArrayList<String> back_item;
    ArrayList<String> predict;
    String state;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPre_item(ArrayList<String> pre_item) {
        this.pre_item = pre_item;
    }

    public ArrayList<String> getPre_item() {
        return pre_item;
    }

    public void setBack_item(ArrayList<String> back_item) {
        this.back_item = back_item;
    }

    public ArrayList<String> getBack_item() {
        return back_item;
    }

    public void setPredict(ArrayList<String> predict) {
        this.predict = predict;
    }

    public ArrayList<String> getPredict() {
        return predict;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
