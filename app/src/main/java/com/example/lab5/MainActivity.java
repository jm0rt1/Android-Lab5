package com.example.lab5;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Handler mHandler;
    TextView textView;
    ProgressBar progressBar;
    Random r;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup Actions
        createNotificationChannel();
        initNavDrawer();
        initThreadingComponents();
        initActivityResultLauncher();

    }

    private void initActivityResultLauncher() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK)
                        {
                            Intent data = result.getData();
                            ImageView image = (ImageView) findViewById(R.id.image_view);
                            Bitmap b = (Bitmap) data.getExtras().get("data");
                            image.setImageBitmap(b);
                        }
                    }
                });
    }

    private void initThreadingComponents() {
        textView = (TextView) findViewById(R.id.text_view);
        progressBar = (ProgressBar) findViewById(R.id.task_progress_bar);
        mHandler = new Handler();
        r = new Random();
    }

    private void initNavDrawer() {

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }


    public void doVeryDifficultWork(View view)
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

                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        Log.d("Notification thread", "sleep failure");
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.camera_import:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                launcher.launch(takePictureIntent);
                return true;

            default:
                return false;

        }
    }
    private void createNotificationChannel()
    {
        //only create the notification channel on API 26+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("alert", "name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(View V){

        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"alert");

        builder.setContentTitle("Notifying You");
        builder.setContentText("Just wanted to notify you");
        builder.setSilent(false);
        builder.setSmallIcon(R.drawable.ic_launcher_background);

        manager.notify(1, builder.build());
    }


    public void takePicture(View view)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        launcher.launch(takePictureIntent);
    }
}