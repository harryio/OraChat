package com.harryio.orainteractive.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.harryio.orainteractive.R;
import com.harryio.orainteractive.Utils;
import com.harryio.orainteractive.ui.account.AccountFragment;
import com.harryio.orainteractive.ui.adapter.ViewPagerAdapter;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements AccountFragment.OnFragmentInteractionListener {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindString(R.string.edit_user_details_progress)
    String editProfileDialogMessage;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(editProfileDialogMessage);
    }

    @Override
    public void showMessage(String message) {
        Utils.showMessage(this, message);
    }

    @Override
    public void showProfileEditDialog() {
        progressDialog.show();
    }

    @Override
    public void hideProfileEditDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
