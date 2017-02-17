package jp.techacademy.wakabayashi.kojiro.taskapp2;

import android.app.Application;

import io.realm.Realm;


//アプリケーション起動時（Applicationオブジェクトが作成されたとき）にデフォルトとして設定することで、アプリケーション全体で同じ設定のRealmを使用することができます。
//Applicationクラスを継承したTaskAppクラスを作成するだけではこのクラスは使われることはないため、AndroidManifest.xmlに１行追加する必要があります。
/**
 * Created by wkojiro on 2017/02/16.
 */

public class TaskApp extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        Realm.init(this);
    }
}
