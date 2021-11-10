package ai.axcess.axcessshopper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Dashboard extends AppCompatActivity {

    TextView shopper;
    Button llogout;
    String fname;
    String barid;
    String cunq;
    String postaction;
    public Handler handler;
    AlertDialog dialog;
    Button todaymorning;
    Button todayafternoon;
    Button todayevening;
    Button tommorowmorning;
    Button tommorowafternoon;
    Button tommorowevening;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();

        cunq = shared.getString("shopperid", "");
        int j = shared.getInt("key", 0);

        if(j > 0) {
            fname = shared.getString("sendfname", "");
            cunq = shared.getString("shopperid", "");
            barid = shared.getString("barid", "");

        }else {
            fname = getIntent().getExtras().getString("sendfname");
            cunq = getIntent().getExtras().getString("shopperid");
            barid = shared.getString("barid", "");
        }


        shopper = (TextView)findViewById(R.id.shoppername);
        shopper.setText(fname);
        llogout = (Button)findViewById(R.id.logout);


        todaymorning = (Button)findViewById(R.id.todaymorning);
        todayafternoon = (Button)findViewById(R.id.todayafternoon);
        todayevening = (Button)findViewById(R.id.todayevening);
        tommorowmorning = (Button)findViewById(R.id.tommorowmorning);
        tommorowafternoon = (Button)findViewById(R.id.tommorowafternoon);
        tommorowevening = (Button)findViewById(R.id.tommorowevening);

        try {


            Log.i("[print]", "https://axcess.ai/barapp/shopper_getbuyslots.php?barid=" + barid );
            doGetRequest("https://axcess.ai/barapp/shopper_getbuyslots.php?barid=" + barid );
        } catch (IOException e) {
            e.printStackTrace();
        }





        llogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shared.edit().clear().commit();


                dialog = new SpotsDialog.Builder()
                        .setMessage("Please Wait")
                        .setContext(Dashboard.this)
                        .build();
                dialog.show();

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run(){


                        Intent intent = new Intent(Dashboard.this, MainActivity.class);
                        startActivity(intent);

                        dialog.dismiss();

                    }
                }, 1000);


            }

        });



        todaymorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String tagname = (String)v.getTag();
                Log.i("seetag", tagname);

                Intent intent = new Intent(Dashboard.this, Listorders.class);
                intent.putExtra("timeslot",tagname);
                startActivity(intent);

           }

        });



        tommorowevening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String tagname = (String)v.getTag();
                Log.i("seetag", tagname);


                Intent intent = new Intent(Dashboard.this, Listorders.class);
                intent.putExtra("timeslot",tagname);
                startActivity(intent);

            }

        });


        tommorowafternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String tagname = (String)v.getTag();
                Log.i("seetag", tagname);

                Intent intent = new Intent(Dashboard.this, Listorders.class);
                intent.putExtra("timeslot",tagname);
                startActivity(intent);

            }

        });



        tommorowmorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String tagname = (String)v.getTag();
                Log.i("seetag", tagname);

                Intent intent = new Intent(Dashboard.this, Listorders.class);
                intent.putExtra("timeslot",tagname);
                startActivity(intent);

            }

        });



        todayevening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String tagname = (String)v.getTag();
                Log.i("seetag", tagname);

                Intent intent = new Intent(Dashboard.this, Listorders.class);
                intent.putExtra("timeslot",tagname);
                startActivity(intent);

            }

        });


        todayafternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String tagname = (String)v.getTag();
                Log.i("seetag", tagname);

                Intent intent = new Intent(Dashboard.this, Listorders.class);
                intent.putExtra("timeslot",tagname);
                startActivity(intent);

            }

        });











    }



    void doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                                Log.i("[print]","error" + e);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        postaction = response.body().string();
                        Log.i("assyn url",postaction);
                        // Do something with the response


                        Log.i("[print]",postaction);
                        postaction = postaction.trim();

                        String[] pieces = postaction.split(Pattern.quote("/"));

                        String d1 = pieces[0];
                            String[] breakd1 = d1.split(Pattern.quote("~"));
                                    String date1 = breakd1[0].trim();
                                    String tag1 = breakd1[1].trim();
                                     String c1 = breakd1[2].trim();

                        String d2 = pieces[1];
                                String[] breakd2 = d2.split(Pattern.quote("~"));
                                String date2 = breakd2[0].trim();
                                String tag2 = breakd2[1].trim();
                                String c2 = breakd2[2].trim();

                        String d3 = pieces[2];
                            String[] breakd3 = d3.split(Pattern.quote("~"));
                            String date3 = breakd3[0].trim();
                            String tag3 = breakd3[1].trim();
                            String c3 = breakd3[2].trim();

                        Log.i("[count]",c3);

                        String d4 = pieces[3];
                            String[] breakd4 = d4.split(Pattern.quote("~"));
                            String date4 = breakd4[0].trim();
                            String tag4 = breakd4[1].trim();
                             String c4 = breakd4[2].trim();



                        String d5 = pieces[4];
                            String[] breakd5 = d5.split(Pattern.quote("~"));
                            String date5 = breakd5[0].trim();
                            String tag5 = breakd5[1].trim();
                            String c5 = breakd5[2].trim();


                        String d6 = pieces[5];
                            String[] breakd6 = d6.split(Pattern.quote("~"));
                            String date6 = breakd6[0].trim();
                            String tag6 = breakd6[1].trim();
                            String c6 = breakd6[2].trim();



                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {



                        todaymorning.setText(date1);
                                todaymorning.setTag(tag1);
                                if(c1.equals("0") ) {
                                    todaymorning.setEnabled(false);
                                }



                        todayafternoon.setText(date2);
                                todayafternoon.setTag(tag2);
                                if(c2.equals("0") ) {
                                    todayafternoon.setEnabled(false);
                                }




                                todayevening.setText(date3);
                                todayevening.setTag(tag3);
                                if(c3.equals("0") ) {
                                    todayevening.setEnabled(false);
                                }



                                tommorowmorning.setText(date4);
                                tommorowmorning.setTag(tag4);
                                if(c4.equals("0") ) {
                                    tommorowmorning.setEnabled(false);
                                }



                                tommorowafternoon.setText(date5);
                                tommorowafternoon.setTag(tag5);
                                if(c5.equals("0") ) {
                                    tommorowafternoon.setEnabled(false);
                                }


                                tommorowevening.setText(date6);
                                tommorowevening.setTag(tag6);
                                if(c6.equals("0") ) {
                                    tommorowevening.setEnabled(false);
                                }


                            }
                        });

                    }
                });
    }








}