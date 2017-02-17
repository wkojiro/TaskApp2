package jp.techacademy.wakabayashi.kojiro.taskapp2;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class InputActivity extends AppCompatActivity {

    //メンバ変数の定義（まとめてできるんだ！）
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button mDateButton, mTimeButton;
    private EditText mTitleEdit, mContentEdit, mCategoryEdit;
    private Task mTask;

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
    }

    private void addTask() {
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
        String category = mCategoryEdit.getText().toString();

        mTask.setTitle(title);
        mTask.setContents(content);
        mTask.setCategory(category);
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
