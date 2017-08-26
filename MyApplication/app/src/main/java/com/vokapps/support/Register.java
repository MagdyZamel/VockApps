package com.vokapps.support;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vokapps.support.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.intercom.android.sdk.Company;
import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.UserAttributes;
import io.intercom.android.sdk.identity.Registration;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Register extends AppCompatActivity {
    EditText full_name , email_address , company_name , mobile_number, password,confirmPassword;
    Button register;
    Typeface typefacee;
    AVLoadingIndicatorView avi; // Progress




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
    }


    //Views and font
    private void initialize(){

        //TypeFace
        typefacee = Typeface.createFromAsset(getAssets(), "sannn.ttf");


/*
        //Action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_name);

        //Change Font
        TextView textView = (TextView) findViewById(R.id.mytext);
        textView.setText("Registration");
        textView.setTypeface(typefacee); */




        //Progress
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);

        avi.hide();

        //Views
        full_name = (EditText) findViewById(R.id.full_name_EditText);
        email_address = (EditText) findViewById(R.id.email_EditText);
        company_name = (EditText) findViewById(R.id.company_name_EditText);
        mobile_number = (EditText) findViewById(R.id.mobile_EditText);
        register = (Button) findViewById(R.id.createUserButton);
        password =  (EditText) findViewById(R.id.password);
        confirmPassword =  (EditText) findViewById(R.id.confirmPassword);



        //Font 4 all Activity views
        full_name.setTypeface(typefacee);
        password.setTypeface(typefacee);
        confirmPassword.setTypeface(typefacee);
        email_address.setTypeface(typefacee);
        company_name.setTypeface(typefacee);
        mobile_number.setTypeface(typefacee);
        register.setTypeface(typefacee);

    }


    public void SignUp(View view) {
        String name = full_name.getText().toString().trim();
        String email = email_address.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String co_name = company_name.getText().toString().trim();
        String mobile_no = mobile_number.getText().toString().trim();
        String password = this.password.getText().toString();
        String confirmPassword = this.confirmPassword.getText().toString().trim();


        if (TextUtils.isEmpty(name)){
            full_name.setError("Required");
        }

        if (TextUtils.isEmpty(email)){
            email_address.setError("Required");
        }else if ( !email.matches(emailPattern)){
            email_address.setError("Invalid email");
        }

        if (TextUtils.isEmpty(co_name)){
            company_name.setError("Required");
        }

        if (TextUtils.isEmpty(mobile_no)){
            mobile_number.setError("Required");
        }


        if (!validateMobile(mobile_no)){
            mobile_number.setError("Invalid Mobile Number");
        }

        if (TextUtils.isEmpty(password)){
            this.password.setError("Required");
        }
        if (TextUtils.isEmpty(confirmPassword)){
            this.confirmPassword.setError("Required");
        }

        if (!password.equalsIgnoreCase(confirmPassword) &&  !TextUtils.isEmpty(confirmPassword)){
            this.confirmPassword.setError("Password doesn't match");
        }


        String pass = this.confirmPassword.getText().toString();
        String confirmPass =  this.password.getText().toString();

            if (!TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(co_name) &&
                !TextUtils.isEmpty(mobile_no) &&
                    validateMobile(mobile_no) &&
                    email.matches(emailPattern) &&
                    pass.equalsIgnoreCase(confirmPass) &&
                    !TextUtils.isEmpty(password)) {





                avi.show();


                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://devs.voktechhosts.com/api/signup.php?email="+email+"&password="+password)
                        .get()
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Register.this.runOnUiThread(new Runnable() {
                            public void run() {
                                avi.hide();
                                Toast.makeText(Register.this, "Internal Server Error", Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Register.this.runOnUiThread(new Runnable() {
                            public void run() {
                                avi.hide();
                            }
                        });


                        try {
                            String responseData = response.body().string();
                            JSONObject json = new JSONObject(responseData);
                            final String owner = json.getString("status");
                            if (owner.equalsIgnoreCase("Successful")) {
                                String name = full_name.getText().toString();
                                String email = email_address.getText().toString().trim();
                                String co_name = company_name.getText().toString();
                                String mobile_no = mobile_number.getText().toString().trim();

                                co_name =  getOnlyStrings(co_name);

                                Registration registration = Registration.create().withEmail(email);
                                Intercom.client().registerIdentifiedUser(registration);
                                Company company = new Company.Builder()
                                        .withName(co_name)
                                        .withCompanyId(co_name)
                                        .build();
                                UserAttributes userAttributes = new UserAttributes.Builder()
                                        .withCompany(company).withEmail(email).withPhone(mobile_no).withName(name)
                                        .build();
                                Intercom.client().updateUser(userAttributes);
                                Intercom.client().handlePushMessage();



                                SharedPreferences sp = getSharedPreferences("intercomPrefs", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean("loggedIn", true);
                                editor.commit();
                                Intent goToHome = new Intent(Register.this , Home.class);
                                startActivity(goToHome);
                                finish();


                            }
                            else {
                                Register.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(Register.this, "email already used", Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        } catch (JSONException e) {
                            Register.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Register.this, "Internal Server Error", Toast.LENGTH_LONG).show();
                                }
                            });

                        }


                    }
                });










        }








    }


    public boolean validateMobile(String mobile) {
        return mobile.length() == 8;
    }



    private   String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String str = matcher.replaceAll("").toLowerCase();
        return str;
    }




}
