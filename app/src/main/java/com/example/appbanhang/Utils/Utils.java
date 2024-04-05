package com.example.appbanhang.Utils;

import com.example.appbanhang.Model.GioHang;
import com.example.appbanhang.Model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    //Them dia chi localhost
    public static final String BASE_URL="http://192.168.0.108192.168.47.1/banhang/";
    public static List<GioHang> mangGiohang;
    public static List<GioHang> mangMuahang = new ArrayList<>();
    public static User currentUser = new User();
}
