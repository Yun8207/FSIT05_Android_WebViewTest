package tw.alex.webviewtest;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
   // private EditText myname;
    private LocationManager lmgr;
    private MyListener myListener;
    private TextView username;
    private UIHandler uiHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,}, 123);
        }else{
            init();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){

        uiHandler = new UIHandler();

        lmgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        myListener = new MyListener();
        lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, myListener);



        //myname = findViewById(R.id.myname);
        webView = findViewById(R.id.webview);
        username = findViewById(R.id.username);
        initWebView();
    }

    @Override
    public void finish() {
        lmgr.removeUpdates(myListener);
        super.finish();
    }

    private class MyListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            //Log.v("alex",lat +"x" +lng);
            webView.loadUrl("javascript:gotoWhere("+ lat +","+ lng +")");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private void initWebView(){
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.addJavascriptInterface(new MyJSObject(), "alex");

        webView.loadUrl("file:///android_asset/Alex01.html");
        //webView.loadUrl("http://www.iii.org.tw");
    }

    public class  MyJSObject {

        @JavascriptInterface
        public void callFromJs(String username){
            Message message = new Message();
            Bundle data = new Bundle();
            data.putString("username", username);
            message.setData(data);
            uiHandler.sendMessage(message);
           // MainActivity.this.username.setText(username);
//            new AlertDialog.Builder(MainActivity.this)
//                    .setMessage("Welcome, " + username)
//                    .show();

            //Log.v("alex", "call from JS " +username);

        }
    }

    private class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String name = msg.getData().getString("username");
            username.setText(name);
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Welcome, " +name)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            new AlertDialog.Builder(this)
                    .setMessage("Exit?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
            //super.onBackPressed();
        }
    }

    public void test1(View view) {
        //String name = myname.getText().toString();
        webView.loadUrl("javascript:gotoKD()");
        //webView.loadUrl("javascript:test4('"+ name + "')");

    }
}
