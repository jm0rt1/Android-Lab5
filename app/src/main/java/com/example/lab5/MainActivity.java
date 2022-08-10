package com.example.lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Handler mHandler;
    TextView textView;
    ProgressBar progressBar;
    Random r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text_view);
        progressBar = (ProgressBar) findViewById(R.id.task_progress_bar);
        mHandler = new Handler();
        r = new Random();
    }


    public void StartCount(View view)
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=1; i<=100; i++)
                {
                    final int finali = i;
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(Integer.toString(finali));
                        }
                    });

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(finali);
                        }
                    });

                    try {
                        int temp = r.nextInt(10)+1;
                        Thread.sleep(20*temp);
                    }
                    catch (InterruptedException e)
                    {
                        Log.d("Notification thread", "sleep failure");
                    }
                }
            }
        }).start();
    }
}