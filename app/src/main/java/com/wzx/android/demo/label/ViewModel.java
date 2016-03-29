package com.wzx.android.demo.label;

import java.io.Serializable;

/**
 * Created by wang_zx on 2015/12/22.
 */
public class ViewModel implements Serializable,Cloneable{

    public void clean(){

    }
    public ViewModel(){

    }

    public boolean isValidate(){
        return true;
    }

    public Object clone() throws CloneNotSupportedException
    {
        ViewModel bean = (ViewModel)super.clone();
        return bean;
    }
}
