package ai.axcess.axcessshopper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.device.ScanDevice;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.squareup.picasso.Picasso;

import com.squareup.picasso.Picasso;

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
    Button bntclick;
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
                int checkResource = R.drawable.ic_baseline_playlist_add_check_24;


                System.out.println("number scantxt : "+ scantext );
                // String[] separated = scantext.split(Pattern.quote("|"));

                String[] dishout = scantext.split(Pattern.quote("@"));

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
                newtxt.setPadding(0, 0, 0, 5 );
                newtxt.setTypeface(null, Typeface.BOLD);
                newtxt.setGravity(Gravity.CENTER);
                layout.addView(newtxt);

                int idup;
                int btnnumb;
                System.out.println(makebtn + "number buttons: " + Arrays.toString(dishout));
                for (int i = 0; i < makebtn; i++) {
                    idup = i + 20;
                    btnnumb = i + 1;



                    tline = dishout[i] ;
                    String[] sbtns = tline.split("~");
                    String returnorder = sbtns[0];
                     String upc = sbtns[1];
                     upc_count = sbtns[2];
                    String thisaisle = sbtns[3];
                    String thisimg = sbtns[4];
                    String btncolor = sbtns[5];



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

                    ImageView image = new ImageView(getApplicationContext());
                   //image.setLayoutParams(params);
                    image.setMaxHeight(1);
                    image.setMaxWidth(1);

                    //image.setImageDrawable(drawable);
                    layout.addView(image);
                    Picasso.with(getApplicationContext())
                            .load(thisimg)
                            .resize(600, 400) // resizes the image to these dimensions (in pixel)
                            .centerInside()
                            //.fit()
                            .into(image);


                    Button btn = new Button(getApplicationContext());
                    btn.setId(btnnumb);
                    btn.setTag(orderblock + "~" + upc + "~" + btnnumb);
                    final int theorder = btn.getId();
                    btn.setText(Html.fromHtml(thisaisle + returnorder + " (" +  upc_count  + ")") );
                    params.width = 300;
                    btn.setTextSize(16);
                    btn.setLayoutParams(acceptbtn);
                    btn.setPadding(5, 3, 5, 3 );
                    btn.setBackgroundColor(getResources().getColor(R.color.gray));
                    btn.setTextColor(getResources().getColor(R.color.black));
                    btn.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    btn.setCompoundDrawablePadding(8);

                    switch(btncolor){
                        case "default":
                            //btn.setBackgroundColor(Color.parseColor("#ff2233"));
                            break;
                        case "amber":
                            btn.setBackgroundColor(Color.parseColor("#cfbe06"));
                            break;
                        case "green":
                            btn.setBackgroundColor(Color.parseColor("#4dc405"));
                            break;


                    }

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


                int dontbtn = 999;
                Button done = new Button(getApplicationContext());
                done.setId(dontbtn);
                done.setTag(orderblock + "~" + dontbtn);
                final int theorder = done.getId();
                done.setText(Html.fromHtml("Set as Completed" ));
                params.width = 300;
                done.setTextSize(16);
                done.setLayoutParams(acceptbtn);
                done.setPadding(5, 3, 5, 3 );
                done.setBackgroundColor(getResources().getColor(R.color.gray));
                done.setTextColor(getResources().getColor(R.color.black));
                done.setCompoundDrawablesWithIntrinsicBounds(checkResource, 0, 0, 0);
                done.setCompoundDrawablePadding(8);
                done.setBackgroundColor(Color.parseColor("#4dc405"));
                layout.addView(done);

                done.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        final String tagname = (String)view.getTag();
                        Log.i("accept tag", tagname);




                        AlertDialog.Builder builder = new AlertDialog.Builder(Runorders.this);
                        builder.setTitle("Confirm");

                        builder.setMessage(Html.fromHtml("Confirm shopping completed"));

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                // Do nothing, but close the dialog
                                dialog.dismiss();
                                System.out.println("action numbers tag "+ tagname);
                                String releaseid = tagname;



                                try {

                                    Log.i("[print]", "https://axcess.ai/barapp/shopper_docomplete.php?orderdetails=" + tagname  );
                                    doCompletion("https://axcess.ai/barapp/shopper_docomplete.php?orderdetails=" + tagname);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                Intent intent = new Intent(Runorders.this, Listorders.class);
                                //intent.putExtra("userid",releaseid);
                                intent.putExtra("timeslot",timerslot);
                                startActivity(intent);



                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();


                    }
                });




        }
        });

    }



    void doCompletion(String url) throws IOException {
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



                    }
                });
    }


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
            String[] pieces = outupc.split(Pattern.quote("~"));

            String thisupc = pieces[0];
            String btnid = pieces[1];

            int myNum = 0;

            try {
                myNum = Integer.parseInt(btnid);
            } catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }

            //myNum = myNum + myNum + 1;
            //Toast.makeText(getApplicationContext(), "xpressed upc: " + myNum  , Toast.LENGTH_LONG).show();

            //Toast.makeText(getApplicationContext(), "barcode: " + barcodeStr + "UPC: "+ thisupc , Toast.LENGTH_LONG).show();
            sm.stopScan();

            if(!thisupc.equals(barcodeStr)) {
                /*
                LinearLayout layout = (LinearLayout) findViewById(R.id.scnf_run);
                View v = null;
                v = layout.getChildAt(myNum);
                v.setBackgroundColor(Color.parseColor("#ff2233"));
                */

                final Button btn_tmp;
                btn_tmp = (Button) findViewById(myNum);
                btn_tmp.setBackgroundColor(Color.parseColor("#ff2233"));

            }else {

                try {


                    Log.i("[print]", "https://axcess.ai/barapp/shopper_scanupc.php?upc=" + barcodeStr + "&ordertoken=" + ordertoken + "&btnpress=" + myNum + "&thisupc=" + thisupc);
                    doScanupc("https://axcess.ai/barapp/shopper_scanupc.php?upc=" + barcodeStr + "&ordertoken=" + ordertoken + "&btnpress=" + myNum + "&thisupc=" + thisupc);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }



        }
    };


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

                        String[] pieces = postaction.split(Pattern.quote("~"));

                        String output = pieces[0].trim();
                        String btnid = pieces[1].trim();
                        String colorout = pieces[2].trim();
                        String updatedcount = pieces[3].trim();
                        String updatedbtn = pieces[4].trim();
                        Log.i("[print]",output);
                        int myNum = 0;

                        try {
                            myNum = Integer.parseInt(btnid);
                        } catch(NumberFormatException nfe) {
                            System.out.println("Could not parse " + nfe);
                        }

                        //myNum = myNum + 1;
                        //myNum = myNum + myNum + 1;
                        //Toast.makeText(getApplicationContext(), "pressed upc: " + myNum  , Toast.LENGTH_LONG).show();
                        System.out.println("element :" + myNum + " color: " + colorout);
                        LinearLayout layout = (LinearLayout) findViewById(R.id.scnf_run);
                        View v = null;
                        v = layout.getChildAt(myNum);

                        //TextView textOut = (TextView)row.findViewById(R.id.textout);
                        //bntclick = (Button)findViewById(R.id.);
                        final Button btn_tmp;
                        btn_tmp = (Button) findViewById(myNum);

                        //int get_bgn = 1;
                        //btn_tmp = (Button) findViewById(get_bgn);
                        //btn_tmp.setText("Change this");

                        switch(colorout){
                            case "red":
                            //v.setBackgroundColor(Color.parseColor("#ff2233"));
                                btn_tmp.setBackgroundColor(Color.parseColor("#ff2233"));
                                btn_tmp.setText(Html.fromHtml(updatedbtn));
                     break;
                            case "amber":
                           // v.setBackgroundColor(Color.parseColor("#cfbe06"));
                                btn_tmp.setBackgroundColor(Color.parseColor("#cfbe06"));
                                btn_tmp.setText(Html.fromHtml(updatedbtn));
                         break;
                            case "green":
                               // v.setBackgroundColor(Color.parseColor("#4dc405"));
                                btn_tmp.setBackgroundColor(Color.parseColor("#4dc405"));
                                btn_tmp.setText(Html.fromHtml(updatedbtn));
                                break;


                        }



                    }
                });
    }






}