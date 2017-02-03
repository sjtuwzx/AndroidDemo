package com.wzx.android.demo;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wzx.android.demo.v2.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wangzhenxing on 16/8/13.
 */
public class LayoutInflateActivity extends Activity {

    private ViewGroup mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_inflate);
        mContainer = (ViewGroup) findViewById(R.id.container);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = LayoutInflater.from(LayoutInflateActivity.this);
                try {
                    String[] files = getResources().getAssets().list("");
                    for (String file : files) {
                        System.out.println(file);
                    }
                    String layout = getFromAssets("list_item.xml");
                    System.out.println(layout);

                    XmlPullParser parser = getParser(layout);
                    View view = inflater.inflate(parser, mContainer, false);
                    mContainer.addView(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }



    /**
     * return XmlPullParser
     * @param xml compiled XML encoded in base64
     * @return XmlPullParser
     */
    public static XmlPullParser getParser(String xml) {

        try {

            // XmlBlock block = new XmlBlock(LAYOUT.getBytes("UTF-8"));
            Class<?> clazz = Class.forName("android.content.res.XmlBlock");
            Constructor<?> constructor = clazz.getDeclaredConstructor(byte[].class);
            constructor.setAccessible(true);
            Object block = constructor.newInstance(xml.getBytes());

            // XmlPullParser parser = block.newParser();
            Method method = clazz.getDeclaredMethod("newParser");
            method.setAccessible(true);
            return (XmlPullParser) method.invoke(block);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
