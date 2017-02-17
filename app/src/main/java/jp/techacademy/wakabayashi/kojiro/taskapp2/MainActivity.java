package jp.techacademy.wakabayashi.kojiro.taskapp2;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity{

    public final static String EXTRA_TASK = "jp.techacademy.wakabayashi.kojiro.taskapp2.TASK";


    boolean isSearch = false;
    String keyword;


    /* Realm 関連　*/
    private Realm mRealm;
    private RealmResults<Task> mTaskRealmResults;
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            reloadListView();
        }
    };

    /*

    Realmクラスを保持するmRealmを定義します。
    RealmResultsクラスのmTaskRealmResultsはデータベースから取得した結果を保持する変数です。
    RealmChangeListenerクラスのmRealmListenerはRealmのデータベースに追加や削除など変化があった場合に呼ばれるリスナーです。
    onChangeメソッドをオーバーライドしてreloadListViewメソッドを呼び出すようにします。
    */

    EditText mSearchText;

    private ListView mListView;
    private TaskAdapter mTaskAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
            }
        });


        // Realmの設定
        //mRealm = Realm.getDefaultInstance();

        // searcTextの設定
        mSearchText = (EditText) findViewById(R.id.search_edit_text);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                //Log.d("onEditorAction", "actionId = " + actionId + " event = " + (event == null ? "null" : event));

                keyword = mSearchText.getText().toString();
                Log.d("後",keyword);

                search();

                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        Log.d("onEditorAction", "check");
                        // ソフトキーボードを隠す
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
                        Toast.makeText(getApplicationContext(), "editText1", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        //EditTextにリスナーをセット
        /*
        RealmクラスのgetDefaultInstanceメソッドでオブジェクトを取得します。
        そして日付(date)で降順で取得した結果をmTaskRealmResultsに代入します。
        そしてmRealmListenerをaddChangeListenerメソッドで設定します。

         */
        mRealm = Realm.getDefaultInstance();
        mTaskRealmResults = mRealm.where(Task.class).findAll();
        mTaskRealmResults.sort("date", Sort.DESCENDING);
        mRealm.addChangeListener(mRealmListener);


        // ListViewの設定
        mTaskAdapter = new TaskAdapter(MainActivity.this);
        mListView = (ListView) findViewById(R.id.listView1);


        // ListViewをタップしたときの処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 入力・編集する画面に遷移させる
                Log.d("タップ","タップ");
                Task task = (Task) parent.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra(EXTRA_TASK, task.getId());

                startActivity(intent);
            }
        });

        // ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("長押し","長押し");

                /*
                長押しするとLesson4で学んだAlertDialogを表示し、OKを押したら削除、CANCELを押したら何もしないという実装にします。
                選択したセルに該当するタスクと同じIDのものを検索し、その結果（RealmResults）に対して deleteAllFromRealm メソッドを呼ぶことで削除を行います。
                削除する際も追加するときと同様にbeginTransactionメソッドとcommitTransactionメソッドで囲む必要があります。
                 */


                // タスクを削除する
                final Task task = (Task) parent.getAdapter().getItem(position);

                // ダイアログを表示する
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("削除");
                builder.setMessage(task.getTitle() + "を削除しますか");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Task> results = mRealm.where(Task.class).equalTo("id", task.getId()).findAll();

                        mRealm.beginTransaction();
                        results.deleteAllFromRealm();
                        mRealm.commitTransaction();

                        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
                        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this,
                                task.getId(),
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(resultPendingIntent);


                        reloadListView();
                    }
                });
                builder.setNegativeButton("CANCEL", null);

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });


        if (mTaskRealmResults.size() == 0) {
            // アプリ起動時にタスクの数が0であった場合は表示テスト用のタスクを作成する
            addTaskForTest();
        }

        reloadListView();
    }

    private void search(){

        mRealm = Realm.getDefaultInstance();
        mTaskRealmResults = mRealm.where(Task.class).equalTo("category", keyword).findAll();
        mTaskRealmResults.sort("date", Sort.DESCENDING);
        mRealm.addChangeListener(mRealmListener);


        // ListViewの設定
       // mTaskAdapter = new TaskAdapter(MainActivity.this);
       // mListView = (ListView) findViewById(R.id.listView1);

        reloadListView();

    }

    
    private void reloadListView() {

        ArrayList<Task> taskArrayList = new ArrayList<>();

        // 後でTaskクラスに変更する
        /*
        ArrayList<String> taskArrayList = new ArrayList<>();
        taskArrayList.add("aaa");
        taskArrayList.add("bbb");
        taskArrayList.add("ccc");
         */

        for (int i = 0; i < mTaskRealmResults.size(); i++) {
            if (!mTaskRealmResults.get(i).isValid()) continue;

            Task task = new Task();

            task.setId(mTaskRealmResults.get(i).getId());
            task.setTitle(mTaskRealmResults.get(i).getTitle());
            task.setContents(mTaskRealmResults.get(i).getContents());
            task.setCategory(mTaskRealmResults.get(i).getCategory());
            task.setDate(mTaskRealmResults.get(i).getDate());

            taskArrayList.add(task);
        }

        mTaskAdapter.setTaskArrayList(taskArrayList);
        mListView.setAdapter(mTaskAdapter);
        mTaskAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }

    private void addTaskForTest() {
        Task task = new Task();
        task.setTitle("作業");
        task.setContents("プログラムを書いてPUSHする");
        task.setCategory("");
        task.setDate(new Date());
        task.setId(0);
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(task);
        mRealm.commitTransaction();
    }

}
