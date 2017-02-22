package jp.techacademy.wakabayashi.kojiro.taskapp2;


import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wkojiro on 2017/02/20.
 */



public class Category extends RealmObject implements Serializable {
    private String name;


    @PrimaryKey
    private  int id;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.name;            // What to display in the Spinner list.
    }

}
