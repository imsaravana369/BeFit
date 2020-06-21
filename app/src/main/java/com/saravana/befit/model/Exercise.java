package com.saravana.befit.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
    private String name;
    private int imgId;
    private int minute;

    public Exercise(){
        //Empty Constructor
    }

    public Exercise(String name, int imgId, int minute) {
        this.name = name;
        this.imgId = imgId;
        this.minute = minute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
    public void incrementMinutes(){
        this.minute+=1;
    }
    public void clearMinutes(){
        this.minute=0;
    }
    public void decrementMinutes(){
        this.minute-=1;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "name='" + name + '\'' +
                ", imgId=" + imgId +
                ", minute=" + minute +
                '}';
    }
    public Exercise(Parcel parcel){
        this.name = parcel.readString();
        this.imgId = parcel.readInt();
        this.minute = parcel.readInt();
    }
    public static Parcelable.Creator CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel source) {
            return new Exercise(source);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    } ;

    @Override
    public int describeContents() {
        return 0; // 0 or CONTENT_FILE_DESCRIPTOR
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(this.name);
         dest.writeInt(this.imgId);
         dest.writeInt(this.minute);
    }
}
