package ai.axcess.axcessshopper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Listorders extends AppCompatActivity {
    Button back;
    String fname;
    String cunq;
    String responseBody;
    ProgressBar progressBar;
    String postaction;
    String barid;
    String timerslot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listorders);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences shared = getSharedPreferences("autoLogin", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = shared.edit();

        fname = shared.getString("sendfname", "");
        cunq = shared.getString("shopperid", "");
        barid = shared.getString("barid", "");

        timerslot = getIntent().getExtras().getString("timeslot");

        back = (Button)findViewById(R.id.backbtn);

        progressBar = (ProgressBar)findViewById(R.id.pbProgress);
        progressBar.setVisibility(View.VISIBLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setBackgroundColor(getResources().getColor(R.color.gray));
                Intent intent = new Intent(Listorders.this, Dashboard.class);

                startActivity(intent);

            }

        });




        try {


            Log.i("[print]", "https://axcess.ai/barapp/shopper_getorders.php?barid=" + barid + "&timeid=" + timerslot  );
            doGetRequest("https://axcess.ai/barapp/shopper_getorders.php?barid=" + barid  + "&timeid=" + timerslot);
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



    private void createLayoutDynamically( String scantext) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                progressBar.setVisibility(View.INVISIBLE);

                LinearLayout layout = (LinearLayout) findViewById(R.id.scnf);
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

                params.setMargins(10, 5, 0, 30);


                int imgResource = R.drawable.ic_baseline_add_shopping_cart_24;


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
                    String orderno = sbtns[0];
                    String price = sbtns[1];
                    String ordertoken = sbtns[2];
                    String outstat = sbtns[3];

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
                    btn.setTag(ordertoken + "~" + timerslot);
                    final int theorder = btn.getId();
                    btn.setText(Html.fromHtml(orderno + " "  + price));
                    params.width = 300;
                    btn.setTextSize(16);
                    btn.setLayoutParams(acceptbtn);
                    btn.setPadding(5, 5, 5, 5 );
                    btn.setBackgroundColor(getResources().getColor(R.color.gray));
                    btn.setTextColor(getResources().getColor(R.color.black));
                    btn.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    btn.setCompoundDrawablePadding(8);

                    if(outstat.equals("Completed")){
                        btn.setBackgroundColor(Color.parseColor("#4dc405"));
                    }


                    layout.addView(btn);





                    btn = ((Button) findViewById(theorder));



                    btn.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View view) {

                            final String tagname = (String)view.getTag();
                            Log.i("accept tag", tagname);

                            Intent intent = new Intent(Listorders.this, Runorders.class);
                            intent.putExtra("orderblock",tagname);
                            startActivity(intent);


                        }
                    });






                }//end make buttons


            }
        });


    }





}