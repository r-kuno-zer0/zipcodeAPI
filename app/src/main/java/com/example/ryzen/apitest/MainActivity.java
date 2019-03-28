package com.example.ryzen.apitest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    TextView textViewUrl;
    EditText editTextUrl;
    Button buttonGet;
    EditText editTextResponse;
    TextView textViewTime;
    long startTime;
    long endTime;
    String str;
    String num;
    String ans_sum;
    Button bt;
    String urlStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        setContentView(ll);

        textViewUrl = new TextView(this);
        textViewTime = new TextView(this);
        editTextUrl = new EditText(this);
        buttonGet = new Button(this);
        editTextResponse = new EditText(this);
        buttonGet.setText("送信");
        EditText et = new EditText(this);
        bt = new Button(this);

        //ll.addView(bt);
        //ll.addView(textViewTime);
       // ll.addView(textViewUrl);
        //ll.addView(editTextUrl);
        ll.addView(buttonGet);
        ll.addView(editTextResponse);
        new IntentIntegrator(this).initiateScan();


        buttonGet.setOnClickListener(new View.OnClickListener(){
        public void onClick(View view){
            new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlStr);
                    // 処理開始時刻
                    Log.d("test",urlStr);
                    startTime = System.currentTimeMillis();
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    str = InputStreamToString(con.getInputStream());
                    // 処理終了時刻
                    endTime = System.currentTimeMillis();
                   // Log.d("HTTPtest", str);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // editTextResponse.setText(String.valueOf(str));
                            textViewTime.setText("処理時間：" + (endTime - startTime) + "ms");
                        }
                    });
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                try {

                 JSONObject json = new JSONObject(str);
                 JSONArray item = json.getJSONArray("results");
                 Log.d("test_item", String.valueOf(item));
                 JSONObject item2 = item.getJSONObject(0);
                 String ans = item2.getString("address1");
                 String ans2 = item2.getString("address2");
                 String ans3 = item2.getString("address3");
                 ans_sum = ans+ans2+ans3;
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }).start();
            editTextResponse.setText(ans_sum);
            Log.i("test","test"+ans_sum);
    }
    });


    }
    // InputStream -> String
    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            Log.d("readQR", result.getContents());
            String test = result.getContents();
            test = test + "/applicationkey";
            Log.d("readQR",test );
            urlStr = result.getContents();
            Log.d("test","test"+urlStr);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
