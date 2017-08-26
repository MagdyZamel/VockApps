package com.vokapps.support;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vokapps.support.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.intercom.android.sdk.Company;
import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.UserAttributes;
import io.intercom.android.sdk.identity.Registration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    EditText  email_address ,password;
    Button register , signUp , facebook;
    Typeface typefacee;
    ImageView back;
    AVLoadingIndicatorView avi; // Progress

    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //TypeFace
        typefacee = Typeface.createFromAsset(getAssets(), "sannn.ttf");




        //views
        email_address=(EditText)findViewById(R.id.full_name_EditText);
        password=(EditText)findViewById(R.id.password);

        //Font 4 all Activity views
        email_address.setTypeface(typefacee);
        password.setTypeface(typefacee);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.relative);
        signUp = (Button) findViewById(R.id.signUp_btn);
        facebook = (Button) findViewById(R.id.login_facebook);

        //progress
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.hide();
       // avi.show();


        //Change Font
        Typeface toolbaer_name = Typeface.createFromAsset(getAssets(), "sannn.ttf");
        signUp.setTypeface(toolbaer_name);
        facebook.setTypeface(toolbaer_name);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String email=email_address.getText().toString();
                String pass=password.getText().toString();
                if (TextUtils.isEmpty(email)){
                    email_address.setError("Required");
                }else if ( !email.matches(emailPattern)){
                    email_address.setError("Invalid email");
                }

                if (TextUtils.isEmpty(pass)){
                    password.setError("Required");
                }




                if (!TextUtils.isEmpty(email)&&
                        !TextUtils.isEmpty(pass)&&email.matches(emailPattern)){

                    avi.show();
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("http://devs.voktechhosts.com/api/login.php?email="+email+"&password="+pass)
                            .get()
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Login.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    avi.hide();
                                }
                            });
                            Login.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Login.this, "Internal Server Error", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Login.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    avi.hide();
                                }
                            });

                            try {
                                String responseData = response.body().string();

                                Log.d("onResponse", "onResponse: "+ responseData);

                                JSONObject json = new JSONObject(responseData);
                                String owner = json.getString("status");
                                if (owner.equalsIgnoreCase("Successful") ) {

                                    String email = email_address.getText().toString();

                                    Registration registration = Registration.create().withEmail(email);
                                    Intercom.client().registerIdentifiedUser(registration);
                                    Intercom.client().handlePushMessage();



                                    SharedPreferences sp = getSharedPreferences("intercomPrefs", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putBoolean("loggedIn", true);
                                    editor.commit();
                                    Intent goToHome = new Intent(Login.this , Home.class);
                                    startActivity(goToHome);
                                    finish();


                                }
                                else {
                                    Login.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(Login.this, "invaild email or password", Toast.LENGTH_LONG).show();
                                        }
                                    });


                                }
                            } catch (JSONException e) {
                                Login.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(Login.this, "Internal Server Error", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }


                        }
                    });




                }
            }
        });






    }



    //Back Check
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(Login.this, "To exit, press again", Toast.LENGTH_SHORT).show();
    } //End onBackPressed



    public void Register(View view) {
        Intent i = new Intent(Login.this , Register.class);
        startActivity(i);

        //Progress
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.hide();


    }
}
