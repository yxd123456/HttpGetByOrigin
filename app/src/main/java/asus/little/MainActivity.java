package asus.little;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String URL_STRING = "http://www.baidu.com";
    TextView tv;
    ProgressDialog progressDialog;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            tv.setText((CharSequence) msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        progressDialog = new ProgressDialog(this);
    }

    /**
     * 按下按钮执行GET请求
     */
    public void get(View view){
        progressDialog.show();
        doGet(URL_STRING, new GetString() {
            @Override
            public void get(String str) {
               send(handler, str);
            }
        });
    }

    public <T> void send(Handler handler, T obj){
        Message msg = Message.obtain();
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    private void doGet(final String s, final GetString getString) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(s);
                    HttpURLConnection connection =
                            (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    StringBuilder builder = new StringBuilder();
                    while ((line=reader.readLine())!=null){
                        builder.append(line);
                    }
                    getString.get(builder.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface GetString{
        void get(String str);
    }

}
