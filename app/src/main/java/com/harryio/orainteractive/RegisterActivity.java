package com.harryio.orainteractive;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.name)
    TextInputEditText nameEdittext;
    @BindView(R.id.email)
    TextInputEditText emailEdittext;
    @BindView(R.id.password)
    TextInputEditText passwordEdittext;
    @BindView(R.id.confirm)
    TextInputEditText confirmEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.register, R.id.login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                break;
            case R.id.login:
                break;
        }
    }
}
