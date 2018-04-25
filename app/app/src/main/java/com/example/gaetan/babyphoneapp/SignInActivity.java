package com.example.gaetan.babyphoneapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gaetan.babyphoneapp.api.*;
import com.example.gaetan.babyphoneapp.bluetooth.BluetoothDeviceConnector;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SignInActivity extends AppCompatActivity {

    private static final int REQUEST_CONNECT_DEVICE = 1;

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.errorText)
    TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        ButterKnife.bind(this);
    }

    public void wifi(View view) {
        startActivity(new Intent(this, BluetoothActivity.class));
        finish();
    }

    public void check(View view) {
        UserApi api = new UserApi();

        api.signin(
                email.getText().toString(),
                password.getText().toString(),
                (token) -> {
                    startActivity(new Intent(this, FluxVideoActivity.class));
                    finish();
                },
                (error) -> errorText.setText(error)
        );
    }

    public void signUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }
}
