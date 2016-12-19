package com.frank;

import java.util.ArrayList;

/**
 * Created by FH on 2015/12/24.
 */
public class BackObject {
    public Error error;
    public String errorMsg;
    public ArrayList arrayList;
    public int totalPageNum;
    public int totalNum;
    public Object obj , obj2;

    public BackObject(Error error) {
        this.error = error;
    }

    public BackObject(ArrayList arrayList, Error error, int totalPageNum, int totalNum) {
        this.arrayList = arrayList;
        this.error = error;
        this.totalPageNum = totalPageNum;
        this.totalNum = totalNum;
    }

    public BackObject(Error error, String errorMsg) {
        this.error = error;
        this.errorMsg = errorMsg;
    }
    public BackObject(Error error, Object obj){
        this.error = error;
        this.obj = obj;
    }

    public BackObject(Error error, Object obj , Object obj2){
        this.error = error;
        this.obj = obj;
        this.obj2 = obj2;
    }

    @Override
    public String toString() {
        return "error:" + error + "  errorMsg:" + errorMsg + "  totalNum:" + totalNum + "  totalPageNum:" + totalPageNum + "  arrayList:" + arrayList;
    }

    public enum Error {
        SUCCESS,
        TOKEN_EXPIRED,
        JSON_EXCEPTION,
        NET_ERROR,
        NO_RESULT,
        ERROR_WITH_MSG,
        NO_MORE,
        NO_NET,
        OTHER_ERROR
    }
}
