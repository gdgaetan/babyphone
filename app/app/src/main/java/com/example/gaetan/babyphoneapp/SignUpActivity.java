package com.example.gaetan.babyphoneapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.example.gaetan.babyphoneapp.api.*;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.babyphoneId)
    EditText babyphoneId;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.errorText)
    TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        ButterKnife.bind(this);
    }

    public void check(View view) {
        UserApi api = new UserApi();

        api.signup(
                email.getText().toString(),
                password.getText().toString(),
                babyphoneId.getText().toString(),
                (token) -> {
                    startActivity(new Intent(this, FluxVideoActivity.class));
                    finish();
                },
                (error) -> errorText.setText(error)
        );

    }

    public void signIn(View view) {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }
}
