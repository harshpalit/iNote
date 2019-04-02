package com.palit.inote;

public class UserDetails {

    public String mName;
    public String mPhone;
    public String mClass;
    public String mEmail;
    public String[] mNoteBooks;

    public UserDetails(){

    }

    public UserDetails(String mName,String mPhone, String mClass, String mEmail, String[] noteBook){
        this.mName = mName;
        this.mClass = mClass;
        this.mEmail = mEmail;
        this.mPhone = mPhone;
        this.mNoteBooks = noteBook;
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

    public String[] getmNoteBooks(){
        return mNoteBooks;
    }

}
