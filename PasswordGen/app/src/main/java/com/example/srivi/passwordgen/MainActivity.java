package com.example.srivi.passwordgen;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Async.IData{

    Handler handler;
    ProgressDialog progressDialog;
    SeekBar seekCount;
    SeekBar seekLength;
    TextView passLength;
    TextView passCount;
    int count;
    int length;
    TextView password;
    CharSequence[] passwordList;
    Async.IData iData;
    ArrayList<String> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
           // wait(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_screen);
        result = new ArrayList<>(  );
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Progress");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        password = (TextView) findViewById(R.id.tvPassword);

        seekCount = (SeekBar) findViewById(R.id.seekBar);
        seekLength = (SeekBar) findViewById(R.id.seekBar2);
        passCount = (TextView) findViewById(R.id.tvPasscount);
        passLength = (TextView) findViewById(R.id.tvPasslength);

        passCount.setText( String.valueOf(  seekCount.getProgress()+1) );
        passLength.setText( String.valueOf(  seekLength.getProgress()+8) );

        seekCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                passCount.setText(String.valueOf(i+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        seekLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                passLength.setText(String.valueOf(i+8));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder( this );

        findViewById(R.id.buttonThread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMax(seekCount.getProgress()+1);
                progressDialog.setProgress(0);
                progressDialog.show();
                count = Integer.parseInt( passCount.getText().toString() );
                length = Integer.parseInt( passLength.getText().toString() );
                passwordList = new CharSequence[count];

                Log.d("Passwordcount",  String.valueOf(count));
                handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        int curCount = (Integer) message.what;
                        passwordList[curCount-1] = (CharSequence) message.obj;
                        Log.d("Count", String.valueOf(curCount));
                        Log.d("Password", passwordList[curCount-1].toString());
                        progressDialog.setProgress(curCount);
                        if(curCount == count) {
                            progressDialog.dismiss();
                            alertBuilder.setTitle("Select a password");
                            alertBuilder.setItems( passwordList, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    password.setText( passwordList[i] );
                                }
                            } );
                            final AlertDialog alertDialog = alertBuilder.create();
                            alertDialog.show();
                        }

                        return false;
                    }
                });
                count = seekCount.getProgress()+1;
                length = seekLength.getProgress()+8;
                Thread thread1 = new Thread(new DoPass());
                thread1.start();
            }
        });
        findViewById( R.id.buttonAsync ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = Integer.parseInt( passCount.getText().toString() );
                length = Integer.parseInt( passLength.getText().toString() );
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Updating Progress");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.setMax(seekCount.getProgress()+1);
                progressDialog.setProgress(0);
                progressDialog.show();
                new Async(MainActivity.this).execute(count,length);
            }
        } );
    }

    @Override
    public void handleListData(ArrayList<String> result) {
        this.result = result;
        progressDialog.dismiss();
        final AlertDialog.Builder alertBuilder1 = new AlertDialog.Builder( this );
        alertBuilder1.setTitle( "Choose a password" );
        passwordList = new CharSequence[result.size()];
        for (int i=0;i<result.size();i++) {
            passwordList[i] = result.get(i);
        }
        alertBuilder1.setItems( passwordList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                password.setText( passwordList[i] );
            }
        } );
        final AlertDialog alertDialog1 = alertBuilder1.create();
        alertDialog1.show();
    }

    @Override
    public void updateData(int cur) {
        progressDialog.setProgress( cur );
    }

    class DoPass implements Runnable {

        @Override
        public void run() {
            int i;
            for (i = 1; i <= count; i++) {
                String password = Util.getPassword(length);
                Message message = new Message();
                message.obj = password;
                message.what = i;
                handler.sendMessage(message);
            }
        }
    }
}