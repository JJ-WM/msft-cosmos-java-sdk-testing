package com.example.common;

public class TestItem {

    String id;
    String mypk;
    public TestItem(String id, String mypk) {
        this.id = id;
        this.mypk = mypk;
    }

    public TestItem() {
    }

    public String getId() {
        return id;
    }

    public String getMypk() {
        return mypk;
    }
}
