package com.example.todoapp.Model;

public class CongViec {
    public String ten;
    public boolean check;

    public CongViec() {
    }

    public CongViec(String ten, boolean check) {
        this.ten = ten;
        this.check = check;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
