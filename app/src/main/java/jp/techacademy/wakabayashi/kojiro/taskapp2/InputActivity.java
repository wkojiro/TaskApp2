package jp.techacademy.wakabayashi.kojiro.taskapp2;


/*
Spinnerの参考サイト
http://androidguide.nomaki.jp/html/widget/spinner/spinnerMain.html

 */


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;



public class InputActivity extends AppCompatActivity {

    //メンバ変数の定義（まとめてできるんだ！）
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button mDateButton, mTimeButton;
    private EditText mTitleEdit, mContentEdit, mCategoryEdit;
    private Task mTask;
    private static final int REQUEST_CODE = 1;
    public int category_id;

    public String category_name;

    private Realm mCategoryRealm; //Realmクラスを保持するmCategoryRealmを定義します。
    private RealmResults<Category> mCategoryRealmResults; //RealmResultsクラスのmCategoryRealmResultsはデータベースから取得した結果を保持する変数です。
    private RealmChangeListener mCategoryRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {

            //reloadCategory();
            Log.d("Categoryが","変わった");
        }
    };



    //Dateをクリック
    private View.OnClickListener mOnDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(InputActivity.this, //DataPickerを生成
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;
                            String dateString = mYear + "/" + String.format("%02d",(mMonth + 1)) + "/" + String.format("%02d", mDay);
                            mDateButton.setText(dateString);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    };

    //時計をクリック
    private View.OnClickListener mOnTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(InputActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mHour = hourOfDay;
                            mMinute = minute;
                            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
                            mTimeButton.setText(timeString);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    };

    private View.OnClickListener mOnDoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addTask();
            finish(); //mOnDoneClickListenerではRealmに保存/更新したあと、finishメソッドを呼び出すことでInputActivityを閉じて前の画面（MainActivity）に戻ります。
        }
    };



    /*

    onCreateメソッドではまずActionBarの設定、その他のUI部品の設定を行います。
    これから、クラス（ファイル）の垣根を超えて、Task を渡す必要が出てきます。そのとき活躍するのが Intent です。
    Intent によって Task の id を渡したり (putExtra())、受け取ったり (getIntExtra()) します。
    Realm のドキュメントにも、Intent による Realm オブジェクトの渡し方 が載っています。
    ここでは既に渡し終わっていると仮定して（後ほど渡すほうも学びます）、受け取る方から実装していきます。
     */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                // 設定ボタン押下処理
                Intent intent = new Intent(InputActivity.this, CategoryActivity.class);

                startActivityForResult(intent, REQUEST_CODE);

                Log.d("新しいページへ","カテゴリ追加ページ");
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //SecondActivityから戻ってきた場合
            case (REQUEST_CODE):
                if (resultCode == RESULT_OK) {
                    Log.d("もどってきた","もどってきた");
                    reloadCategory();

                } else if (resultCode == RESULT_CANCELED) {
                    //キャンセルボタンを押して戻ってきたときの処理
                } else {
                    //その他
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // ActionBarを設定する
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // UI部品の設定
        mDateButton = (Button)findViewById(R.id.date_button);
        mDateButton.setOnClickListener(mOnDateClickListener);
        mTimeButton = (Button)findViewById(R.id.times_button);
        mTimeButton.setOnClickListener(mOnTimeClickListener);
        findViewById(R.id.done_button).setOnClickListener(mOnDoneClickListener);
        mTitleEdit = (EditText)findViewById(R.id.title_edit_text);
        mContentEdit = (EditText)findViewById(R.id.content_edit_text);
        mCategoryEdit = (EditText)findViewById(R.id.category_edit_text);

        Spinner spinner = (Spinner)findViewById(R.id.spinner);



        mCategoryRealm = Realm.getDefaultInstance();
        mCategoryRealmResults = mCategoryRealm.where(Category.class).findAll();
        mCategoryRealmResults.sort("id", Sort.DESCENDING);


        mCategoryRealm.addChangeListener(mCategoryRealmListener);



        /* Spiner 実験中　*/
        // Spinnerオブジェクトを取得
/*
        Spinner spinner = (Spinner)findViewById(R.id.spinner);

        /*
        ArrayList<Category> categoryArrayList = new ArrayList<>();

        for (int i = 0; i < mCategoryRealmResults.size(); i++){
            if (!mCategoryRealmResults.get(i).isValid()) continue;

            Category category = new Category();

            category.setId(mCategoryRealmResults.get(i).getId());
            category.setName(mCategoryRealmResults.get(i).getName());

            categoryArrayList.add(category);
        }

        String[] array = (String[])categoryArrayList.toArray(new String[0]);

        // Adapterの作成
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);



        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

*/
        /**スピナーに追加するアイテム*/
/*
        String[] items = {
                "Mouse",
                "Cow",
                "tiger",
                "Rabbit",
                "Dragon"
        };

        ArrayAdapter<String> spinnerAdapter
                = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        ///アダプターを設定


        // 選択されているアイテムのIndexを取得
        int idx = spinner.getSelectedItemPosition();

        // 選択されているアイテムを取得
        String item = (String)spinner.getSelectedItem();
        Log.d("Spiner",item);*/
        /* Spiner 実験中　*/


        /*
        // EXTRA_TASK から Task の id を取得して、 id から Task のインスタンスを取得する.
        //このとき、もし EXTRA_TASK が設定されていないと taskId は -1 が代入されます。
        次に mTask = realm.where(Task.class).equalTo("id", taskId).findFirst(); によって、
        Task の id が taskId のものが検索され、findFirst() によって最初に見つかったインスタンスが返され、
         mTask へ代入されます。このとき、 taskId に -1 が入っていると、検索に引っかからず、 mTask には null が代入されます（ realm の id は必ず 0以上のため）

         新規作成の場合は遷移元であるMainActivityから EXTRA_TASK は渡されないので taskId に -1 が代入され、 mTask には null が代入されます。
         nullであれば現在時刻からmYear、mMonth、mDay、mHour、mMinutに値を設定します。

         タスクが渡ってきた場合は更新のため渡ってきたタスクの時間を設定します。
         合わせて、mTitleEditにタイトル、mContentEditに内容を設定し、mDateButtonに日付、mTimeButtonに時間をそれぞれ文字列に変換して設定します。

        */
        Intent intent = getIntent();
        int taskId = intent.getIntExtra(MainActivity.EXTRA_TASK, -1);

        Realm realm = Realm.getDefaultInstance();
        mTask = realm.where(Task.class).equalTo("id", taskId).findFirst();
        realm.close();

        if (mTask == null) {
            // 新規作成の場合
            Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
        } else {
            // 更新の場合(編集)の場合
            mTitleEdit.setText(mTask.getTitle());
            mContentEdit.setText(mTask.getContents());
            mCategoryEdit.setText(mTask.getCategory());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mTask.getDate());
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            String dateString = mYear + "/" + String.format("%02d",(mMonth + 1)) + "/" + String.format("%02d", mDay);
            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
            mDateButton.setText(dateString);
            mTimeButton.setText(timeString);
        }

        if (mCategoryRealmResults.size() == 0) {
            // アプリ起動時にタスクの数が0であった場合は表示テスト用のタスクを作成する
            addCategoryForTest();
        }
        reloadCategory();
    }

    private void addCategoryForTest() {
        Category category = new Category();
        category.setName("Android");
        category.setId(0);
        mCategoryRealm.beginTransaction();
        mCategoryRealm.copyToRealmOrUpdate(category);
        mCategoryRealm.commitTransaction();

    }


    private void reloadCategory(){

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        Log.d("カテゴリリロード","リロード");

        ArrayList<Category> categoryArrayList = new ArrayList<>();

        for (int i = 0; i < mCategoryRealmResults.size(); i++){
            if (!mCategoryRealmResults.get(i).isValid()) continue;

            Category category = new Category();

            //category.setId(mCategoryRealmResults.get(i).getId());
            category.setName(mCategoryRealmResults.get(i).getName());

            categoryArrayList.add(category);

        }


        String[] array = new String[categoryArrayList.size()];
        for (int i = 0; i < categoryArrayList.size(); i++) {
            array[i] = String.valueOf(categoryArrayList.get(i));
            Log.d("i",array[i]);
        }


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);



        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                String item = (String) spinner.getSelectedItem();
                Log.d("category_name", item);
                category_name = item;

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


/*
        // 選択されているアイテムのIndexを取得
        category_id = spinner.getSelectedItemPosition();
        Log.d("category_id", String.valueOf(category_id));

        // 選択されているアイテムを取得
        String item = (String)spinner.getSelectedItem();
        category_name = item;

*/

    }

    private void addTask() {

        Log.d("AddTaskcategory_name",category_name);
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

/*
        mTaskがnull、つまり新規作成の場合はTaskクラスを生成し、保存されているタスクの中の最大のidの値に1を足したものを設定します。
        こうすることでユニークなIDを設定することが可能となります。そしてタイトル、内容、日時をmTaskに設定し、データベースに保存します。

  */


        if (mTask == null) {
            // 新規作成の場合
            mTask = new Task();

            RealmResults<Task> taskRealmResults = realm.where(Task.class).findAll();

            int identifier;
            if (taskRealmResults.max("id") != null) {
                identifier = taskRealmResults.max("id").intValue() + 1;
            } else {
                identifier = 0;
            }
            mTask.setId(identifier);
        }

        String title = mTitleEdit.getText().toString();
        String content = mContentEdit.getText().toString();
        //String category = mCategoryEdit.getText().toString();
        String category = category_name;

        mTask.setTitle(title);
        mTask.setContents(content);
        Log.d("AddTaskcategory_name2",category);
        mTask.setCategory(category);
        mTask.setCategory_Id(category_id);
        GregorianCalendar calendar = new GregorianCalendar(mYear,mMonth,mDay,mHour,mMinute);
        Date date = calendar.getTime();
        mTask.setDate(date);


        /*

        Realmでデータを追加、削除など変更を行う場合はbeginTransactionメソッドを呼び出し、削除などの処理、
        そして最後にcommitTransactionメソッドを呼び出す必要があります。
        データの保存・更新はcopyToRealmOrUpdateメソッドを使います。
        これは引数で与えたオブジェクトが存在していれば更新、なければ追加を行うメソッドです。最後にcloseメソッドを呼び出します。

         */


        realm.copyToRealmOrUpdate(mTask);
        realm.commitTransaction();

        realm.close();

        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
        resultIntent.putExtra(MainActivity.EXTRA_TASK, mTask.getId());
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                this,
                mTask.getId(),
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), resultPendingIntent);


    }
}
