package com.palit.harsh.com.e_notebook;


public class UserDetails {

    public String mName;
    public String mPhone;
    public String mClass;
    public String mEmail;

    public UserDetails(){

    }

    public UserDetails(String mName,String mPhone, String mClass, String mEmail){
        this.mName = mName;
        this.mClass = mClass;
        this.mEmail = mEmail;
        this.mPhone = mPhone;
    }


    public String getmName(){
        return mName;
    }

    public String getmPhone(){
        return mPhone;
    }

    public String getmClass(){
        return mClass;
    }

    public String getmEmail(){
        return mEmail;
    }

}
