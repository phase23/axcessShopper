package ai.axcess.axcessshopper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    String cunq;
    String fname;
    String thebarid;
    Button llogin;
    AlertDialog dialog;
    SharedPreferences sharedpreferences;
    int autoSave;
    OkHttpClient client;
    EditText pin;
    private String name;
    String postaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String globaldevice = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);





        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        llogin = (Button)findViewById(R.id.llogin);
        pin = (EditText)findViewById(R.id.pinpass);


        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
        }



        if(!connected) {
            Toast.makeText(getApplicationContext(),"Check Internet & Restart App",Toast.LENGTH_LONG).show();
            Intent nointernet = new Intent(MainActivity.this, nointernet.class);
            startActivity(nointernet);

        }else {


            sharedpreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            int j = sharedpreferences.getInt("key", 0);
            if (j > 0) {
                Intent activity = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(activity);
            }


            llogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String thispin = pin.getText().toString();
                    if (thispin.matches("")) {
                        Toast.makeText(getApplicationContext(), "Enter your password", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    dialog = new SpotsDialog.Builder()
                            .setMessage("Please Wait")
                            .setContext(MainActivity.this)
                            .build();
                    dialog.show();

                    try {


                        Log.i("[print]", "https://axcess.ai/barapp/shopper_shopperlogin.php?&thepin=" + thispin + "&token=" + globaldevice);
                        doGetRequest("https://axcess.ai/barapp/shopper_shopperlogin.php?&thepin=" + thispin + "&token=" + globaldevice);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            });

        }//emd if

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



                        String[] separated = postaction.split("~");
                        String dologin = separated[0];
                        cunq = separated[1];

                        if(dologin.equals("noluck")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // For the example, you can show an error dialog or a toast
                                    // on the main UI thread
                                    Toast.makeText(getApplicationContext(), "Your password is incorrect", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });

                            return;

                        }

                        if(dologin.equals("sucess")){




                            fname = separated[2];
                            thebarid = separated[3];
                            Log.i("pass:unq -- ",cunq + "name: "+ fname+ "barid: " + thebarid);

                            autoSave = 1;
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putInt("key", autoSave);
                            editor.putString("shopperid", cunq);
                            editor.putString("sendfname", fname);
                            editor.putString("barid", thebarid);
                            editor.apply();


                            // Toast.makeText(getApplicationContext(), "Success "+ cunq, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, Dashboard.class);
                            intent.putExtra("shopperid",cunq);
                            intent.putExtra("sendfname",fname);
                            intent.putExtra("barid",thebarid);
                            startActivity(intent);

                            dialog.dismiss();

                        }




                    }
                });
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();

        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        return true;
    }


    public String getlpost() {
        return name;
    }

    public void setlpost(String newName) {
        this.name = newName;
    }


    class CallbackFuture extends CompletableFuture<Response> implements Callback {
        public void onResponse(Call call, Response response) {
            super.complete(response);
        }
        public void onFailure(Call call, IOException e){
            super.completeExceptionally(e);
        }
    }








}