package com.m13.cafe.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class CafeUtils {

    private CafeUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus){
        return new ResponseEntity<String>("{\"message \":\""+responseMessage+ "\"}", httpStatus);

    }

    public static String getUUId(){
        Date date=new Date();
        Long time = date.getTime();
        return "BILL-"+ time;
    }

    public static JSONArray getJSONArrayFromString(String data) throws JSONException{
        JSONArray jsonArray=new JSONArray(data);
        return jsonArray;
    }

    public static Map<String,Object> getMapFromJSON(String data) {
        if (!Strings.isNullOrEmpty(data)){
            return new Gson().fromJson(data,new TypeToken<Map<String,Object>>(){}.getType());

        }
        return new HashMap<>();
    }

    //for checking is this file is actual exist in project or
    public static Boolean isFileExist(String path){
        log.info("inside isFileExist {}",path);
        try{
            //create object of file
            File file=new File(path);
            //it will check is file exist, if exist it will return true else return false
            return (file!=null && file.exists()) ? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
