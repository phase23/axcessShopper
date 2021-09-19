package ai.axcess.axcessshopper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.device.ScanDevice;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Runorders extends AppCompatActivity {
    Button back;
    ProgressBar progressBar;
    String fname;
    String cunq;
    String barid;
    String ordertoken;
    String timerslot;
    String orderblock;
    String postaction;
    ScanDevice sm;
    //String upc;
    String name;
    String upc_count;
    private final static String SCAN_ACTION = "scan.rcv.message";
    private String barcodeStr;


    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            byte[] barocode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            byte[] aimid = intent.getByteArrayExtra("aimid");
            barcodeStr = new String(barocode, 0, barocodelen);
            //showScanResult.append(barcodeStr);
           // showScanResult.append("\n");
           // cardresult = getIntent().getExtras().getString("Returned");
            String outupc = getlpost();
            Toast.makeText(getApplicationContext(), "barcode: " + barcodeStr + "UPC: "+ outupc , Toast.LENGTH_LONG).show();


            sm.stopScan();

            try {


                Log.i("[print]", "https://axcess.ai/barapp/shopper_scanupc.php?upc=" + barcodeStr + "&ordertoken="+ ordertoken );
                doGetRequest("https://axcess.ai/barapp/shopper_scanupc.php?upc=" + barcodeStr + "&ordertoken="+ ordertoken);
            } catch (IOException e) {
                e.printStackTrace();
            }





        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runorders);

        progressBar = (ProgressBar)findViewById(R.id.pbProgress);
        progressBar.setVisibility(View.VISIBLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();

        fname = shared.getString("sendfname", "");
        cunq = shared.getString("shopperid", "");
        barid = shared.getString("barid", "");
        back = (Button)findViewById(R.id.backbtn);

        orderblock = getIntent().getExtras().getString("orderblock");
        String[] pieces = orderblock.split(Pattern.quote("~"));
         ordertoken = pieces[0];
         timerslot = pieces[1];

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setBackgroundColor(getResources().getColor(R.color.gray));
                Intent intent = new Intent(Runorders.this, Listorders.class);
                intent.putExtra("timeslot",timerslot);
                startActivity(intent);

            }

        });



        try {


            Log.i("[print]", "https://axcess.ai/barapp/shopper_listitems.php?orderunq=" + ordertoken + "&timeid=" + timerslot  );
            doGetRequest("https://axcess.ai/barapp/shopper_listitems.php?orderunq=" + ordertoken  + "&timeid=" + timerslot);
        } catch (IOException e) {
            e.printStackTrace();
        }




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


                        try {

                            String[] dishout = postaction.split(Pattern.quote("*"));
                            System.out.println("number tickets: " + Arrays.toString(dishout));
                            //dialog.dismiss();

                            createLayoutDynamically(postaction);


                        } catch(ArrayIndexOutOfBoundsException e) {

                            LinearLayout layout = (LinearLayout) findViewById(R.id.scnf);
                            layout.setOrientation(LinearLayout.VERTICAL);

                            TextView newtxt = new TextView(getApplicationContext());
                            newtxt.setText(Html.fromHtml("No orders"));
                            newtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                            newtxt.setPadding(0, 0, 0, 20 );
                            newtxt.setTypeface(null, Typeface.BOLD);
                            newtxt.setGravity(Gravity.CENTER);
                            layout.addView(newtxt);

                        }








                    }
                });
    }




    void doScanupc(String url) throws IOException {
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






                    }
                });
    }












    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sm != null) {
            sm.stopScan();
            sm.setScanLaserMode(8);
            sm.closeScan();
        }
    }


    public String getlpost() {
        return name;
    }

    public void setlpost(String newName) {
        this.name = newName;
    }



    private void createLayoutDynamically( String scantext) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressBar.setVisibility(View.INVISIBLE);

                LinearLayout layout = (LinearLayout) findViewById(R.id.scnf_run);
                layout.setOrientation(LinearLayout.VERTICAL);


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,180);
                Params1.setMargins(0, 0, 0, 0);

                LinearLayout.LayoutParams acceptbtn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,180);
                acceptbtn.setMargins(0, 0, 0, 60);
                LinearLayout.LayoutParams declinebtn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,180);
                declinebtn.setMargins(0, 0, 0, 60);


                //params.gravity = Gravity.TOP;
                layout.setGravity(Gravity.CENTER|Gravity.TOP);

                params.setMargins(10, 5, 0, 15);


                int imgResource = R.drawable.ic_baseline_view_comfy_24;


                System.out.println("number scantxt : "+ scantext );
                // String[] separated = scantext.split(Pattern.quote("|"));

                String[] dishout = scantext.split(Pattern.quote("/"));

                int makebtn = dishout.length ;
                String tline;


                String locationid;
                String driver_accept;
                String is_pickedup;


                String printwforce = "<br>"
                        + makebtn + " listed";

        /*
        textView.setText(Html.fromHtml(printwforce));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        */

                TextView newtxt = new TextView(getApplicationContext());
                newtxt.setText(Html.fromHtml(printwforce));
                newtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
                newtxt.setPadding(0, 0, 0, 20 );
                newtxt.setTypeface(null, Typeface.BOLD);
                newtxt.setGravity(Gravity.CENTER);
                layout.addView(newtxt);

                int idup;
                System.out.println(makebtn + "number buttons: " + Arrays.toString(dishout));
                for (int i = 0; i < makebtn; i++) {
                    idup = i + 20;



                    tline = dishout[i] ;
                    String[] sbtns = tline.split("~");
                    String returnorder = sbtns[0];
                     String upc = sbtns[1];
                     upc_count = sbtns[2];
                    //String price = sbtns[1];
                    //String ordertoken = sbtns[2];

                    // System.out.println(makebtn + "action listed: " +  printwforce + "col:  " +  imgx );
/*
                    TextView panel = new TextView(getApplicationContext());
                    panel.setText("From: "+ company + "\n\n To: " + locationto );
                    panel.setLayoutParams(Params1);
                    //panel.setWidth(200);
                    panel.setPadding(20, 5, 20, 5 );
                    panel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                    panel.setTextColor(getResources().getColor(R.color.black));
                    panel.setTypeface(null, Typeface.BOLD);
                    panel.setGravity(Gravity.LEFT);
                    layout.addView(panel);
                    panel.setBackgroundColor(getResources().getColor(R.color.gray));
*/



                    Button btn = new Button(getApplicationContext());
                    btn.setId(i);
                    btn.setTag(orderblock + "~" + upc + "~" + i);
                    final int theorder = btn.getId();
                    btn.setText(Html.fromHtml(returnorder + " (" +  upc_count  + ")"));
                    params.width = 300;
                    btn.setTextSize(16);
                    btn.setLayoutParams(acceptbtn);
                    btn.setPadding(5, 3, 5, 3 );
                    btn.setBackgroundColor(getResources().getColor(R.color.gray));
                    btn.setTextColor(getResources().getColor(R.color.black));
                    btn.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    btn.setCompoundDrawablePadding(8);
                    layout.addView(btn);





                    btn = ((Button) findViewById(theorder));



                    btn.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View view) {

                            final String tagname = (String)view.getTag();
                            Log.i("accept tag", tagname);

                            String[] scanpack = tagname.split("~");
                            String upcout = scanpack[2];
                            String btnpressed = scanpack[3];
                            String btndetails = upcout + "~" + btnpressed;
                            setlpost(btndetails);

                            //Intent intent = new Intent(Listorders.this, Runorders.class);
                           // intent.putExtra("orderblock",tagname);
                           // startActivity(intent);
                            sm = new ScanDevice();
                            sm.setOutScanMode(0);//启动就是广播模式

                            sm.openScan();
                            sm.startScan();



                        }
                    });






                }//end make buttons


            }
        });


    }












}