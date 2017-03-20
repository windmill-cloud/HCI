/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginSignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        myToolbar.setTitle("");
        ImageView tv = (ImageView) findViewById(R.id.toolbar_title);
        tv.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent logInIntent = new Intent(LoginSignupActivity.this,
                        SplashScreenActivity.class);
                LoginSignupActivity.this.startActivity(logInIntent);
                LoginSignupActivity.this.finish();
            }
        });
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();

        String type = intent.getStringExtra("TYPE");
        //TextView prompt = (TextView) findViewById(R.id.prompt);
        EditText reEnterPassword = (EditText) findViewById(R.id.password_again);
        Button loginSignupButton = (Button) findViewById(R.id.signup_login_button);

        if (type == null || type.equals("SIGN_UP")) {
            loginSignupButton.setText("Sign up");
            TextView forgotPassword = (TextView) findViewById(R.id.signup_forgot);
            forgotPassword.setVisibility(View.GONE);
        } else if(type.equals("LOG_IN")) {
            //prompt.setText("Log in");
            reEnterPassword.setHeight(0);
            reEnterPassword.setVisibility(View.GONE);
            reEnterPassword.setEnabled(false);
            loginSignupButton.setText("Log in");
        }

        loginSignupButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSignupActivity.this,
                        MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                LoginSignupActivity.this.startActivity(intent);
                LoginSignupActivity.this.finish();
            }
        });

    }

}
