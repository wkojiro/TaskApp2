package jp.techacademy.wakabayashi.kojiro.taskapp2;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryActivity extends AppCompatActivity {

//メンバ変数の定義

    private EditText mNameEdit;
    private Category mCategory;
    Intent intent = new Intent();


    private View.OnClickListener mOnDoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("カテゴリをDBに","保存するためのメソッド01");

            addCategory();


// Activity finished ok, return the data
            setResult(RESULT_OK, intent);

            finish(); //mOnDoneClickListenerではRealmに保存/更新したあと、finishメソッドを呼び出すことでInputActivityを閉じて前の画面（MainActivity）に戻ります。
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // ActionBarを設定する// ツールバーをアクションバーとしてセット
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setTitle("タイトル");
        toolbar.setTitleTextColor(Color.WHITE);


        findViewById(R.id.done_button).setOnClickListener(mOnDoneClickListener);
        mNameEdit = (EditText)findViewById(R.id.name_edit_text);

        /* Realm 関連*/
/*
        Realm realm = Realm.getDefaultInstance();
        mCategory = realm.where(Category.class).findFirst();
        realm.close();
*/
        /*
        if (mCategory == null) {
            // 新規作成の場合


        } else {
            // 更新の場合(編集)の場合
            mNameEdit.setText(mCategory.getName());

        }
        */


    }

    private void addCategory() {

        Log.d("カテゴリをDBに","保存するためのメソッド02");

        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        if (mCategory == null) {
            // 新規作成の場合
            mCategory = new Category();

            RealmResults<Category> categoriesRealmResults = realm.where(Category.class).findAll();

            int identifier;
            if (categoriesRealmResults.max("id") != null) {
                identifier = categoriesRealmResults.max("id").intValue() + 1;
            } else {
                identifier = 0;
            }
            mCategory.setId(identifier);
        }

        String name = mNameEdit.getText().toString();

        mCategory.setName(name);

        realm.copyToRealmOrUpdate(mCategory);
        realm.commitTransaction();

        realm.close();



    }


}
