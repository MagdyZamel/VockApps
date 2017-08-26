package com.vokapps.support;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vokapps.support.R;

import io.intercom.android.sdk.Intercom;

public class Home extends AppCompatActivity {
    Typeface typeface;
    TextView chat , conversation , logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
    }

    public void initialize(){

        //TypeFace
        typeface = Typeface.createFromAsset(getAssets() , "sannn.ttf");


        //Views
        chat = (TextView) findViewById(R.id.chatTextView);
        conversation = (TextView) findViewById(R.id.conversationTextView);
        logout = (TextView) findViewById(R.id.logoutTextView);



        //Font 4 all Activity views
        chat.setTypeface(typeface);
        conversation.setTypeface(typeface);
        logout.setTypeface(typeface);
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
        Toast.makeText(Home.this, "To exit, press again", Toast.LENGTH_SHORT).show();
    } //End onBackPressed

    public void LogOut(View view) {

        SharedPreferences sp = getSharedPreferences("intercomPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("loggedIn", false);
        editor.commit();

        Intercom.client().reset();

        Intent i = new Intent(Home.this , Login.class);
        startActivity(i);

    }

    public void conversationTaped(View view) {

        Intercom.client().displayConversationsList();
    }

    public void ChatWithUsTapped(View view) {
        Intercom.client().displayMessageComposer();


    }

}
