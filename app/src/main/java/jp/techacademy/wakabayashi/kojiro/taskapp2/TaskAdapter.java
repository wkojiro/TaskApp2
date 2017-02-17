package jp.techacademy.wakabayashi.kojiro.taskapp2;

/**
 * Created by wkojiro on 2017/02/16.
 */
/*
Javaのオブジェクトにフィールドがあったとして、そのフィールドに値を設定するメソッドがsetter(せったー)、
そのフィールドの値を取得するメソッドがgetter(げったー)と呼ばれる。
慣習としてsetterはsetXXX(int value)といった様にsetから始まる名前をつけ、引数はひとつ。
戻り値はvoid型。
getterはgetXXX()といった様にgetから始まる名前をつけ、引数はなし。戻り値でフィールドの値を返す。

*/
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends BaseAdapter{
    private LayoutInflater mLayoutInflater; //他のxmlリソースのViewを取り扱うための仕組みであるLayoutInflaterをメンバ変数として定義しておき、コンストラクタを新規に追加して取得しておきます。
    private ArrayList<Task> mTaskArrayList;

    public TaskAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //ActivityクラスのgetSystemServiceメソッドとはシステムレベルのサービスを取得するためのメソッドです。本レッスンではレイアウトのためのサービスとアラームのためのサービスを取得するのに使います。
    }


    public void setTaskArrayList(ArrayList<Task> taskArrayList) {
        mTaskArrayList = taskArrayList;
    }

    @Override
    public int getCount() {
        //アイテム（データ）の数を返す
        return mTaskArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        //アイテム（データ）を返す
        return mTaskArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //アイテム（データ）のIDを返す
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Viewを返す
        //getViewメソッドの引数であるconvertViewがnullのときはLayoutInflaterを使ってsimple_list_item_2からViewを取得します。
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null); //simple_list_item_2はタイトルとサブタイトルがあるセルです。
        }

        TextView textView1 = (TextView) convertView.findViewById(android.R.id.text1);
        TextView textView2 = (TextView) convertView.findViewById(android.R.id.text2);

        // 後でTaskクラスから情報を取得するように変更する
        textView1.setText(mTaskArrayList.get(position).getTitle());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPANESE);
        Date date = mTaskArrayList.get(position).getDate();
        textView2.setText(simpleDateFormat.format(date));

        return convertView;
    }
}
