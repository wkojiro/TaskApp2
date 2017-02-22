package jp.techacademy.wakabayashi.kojiro.taskapp2;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wkojiro on 2017/02/16.
 */



public class Task extends RealmObject implements Serializable {
    private String title;
    private String contents;
    private String category;
    private Integer category_id;
    private Date date;

//シリアライズとはデータを丸ごとファイルに保存したり、TaskAppでいうと別のActivityに渡すことができるようにすることです。

    // ここではモデルを規定して、get,setのメソッドを加えているのかな。


    @PrimaryKey
    private  int id;


    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public int getCategory_Id() {
        return category_id;
    }

    public void setCategory_Id(int category_id) {
        this.category_id = category_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
