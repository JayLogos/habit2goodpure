package kr.co.gubed.habit2goodpure.gpoint.listener;

import org.json.JSONObject;

public interface BuyListener {
    void buyProc(JSONObject jobj, String h, String n, String a, String b);
    void buyNewProc(JSONObject jobj, String input1, String input2, String input3, String input4);
}
