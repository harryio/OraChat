package com.harryio.orainteractive.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.harryio.orainteractive.PrefUtils;
import com.harryio.orainteractive.R;
import com.harryio.orainteractive.Utils;
import com.harryio.orainteractive.rest.OraService;
import com.harryio.orainteractive.rest.OraServiceProvider;
import com.harryio.orainteractive.ui.chat.Chat;
import com.harryio.orainteractive.ui.chat.ChatList;
import com.harryio.orainteractive.ui.chat.ChatListFragment;
import com.harryio.orainteractive.ui.chat.CreateChatRequest;
import com.harryio.orainteractive.ui.chat.MessageListActivity;
import com.harryio.orainteractive.ui.profile.ProfileFragment;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity
        implements ProfileFragment.OnFragmentInteractionListener,
        ChatListFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private static final int ANIM_DURATION = 200;

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindString(R.string.edit_user_details_progress)
    String editProfileDialogMessage;
    @BindString(R.string.create_chat_error)
    String createChatErrorMessage;
    @BindString(R.string.create_chat_progress)
    String createChatProgressMessage;
    @BindString(R.string.error_no_internet_connection)
    String noConnectionMessage;

    private ProgressDialog editProfileDialog, createChatDialog;
    private ViewPagerAdapter pagerAdapter;
    private Subscription subscription;
    private boolean isChatListLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        //Add page change listener on ViewPager so that we can hide/show FAB
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //hide/show FAB only if chat list is successfully fetched from the network
                if (isChatListLoaded) {
                    //Show FAB only on ChatListFragment which we know is at position 0 as there are
                    // only two fragments
                    if (position == 0) {
                        animateInFab();
                    } else if (position == 1) {
                        animateOutFab();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

        //Create dialog which is shown when user saves profile
        editProfileDialog = new ProgressDialog(this);
        editProfileDialog.setMessage(editProfileDialogMessage);

        //Create dialog which is shown when a chat is being created
        createChatDialog = new ProgressDialog(this);
        createChatDialog.setMessage(createChatProgressMessage);
    }

    private void animateInFab() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setInterpolator(new DecelerateInterpolator());
        scaleAnimation.setDuration(ANIM_DURATION);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(scaleAnimation);
    }

    private void animateOutFab() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setDuration(ANIM_DURATION);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setEnabled(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(scaleAnimation);
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        //Get authorization token from SharedPreferences
        PrefUtils prefUtils = PrefUtils.getInstance(this);
        String token = prefUtils.get(PrefUtils.KEY_AUTH_TOKEN, null);

        if (token != null) {
            if (Utils.isNetworkAvailable(this)) {
                //Show create chat dialog before making call to the network
                createChatDialog.show();

                //Make api call to create new chat
                OraService oraService = OraServiceProvider.getInstance();
                subscription = oraService.createChat(token, new CreateChatRequest("A Chat"))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Chat>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                //Dismiss create chat dialog
                                if (createChatDialog.isShowing()) {
                                    createChatDialog.dismiss();
                                }
                                //Notify user that the call failed
                                showMessage(createChatErrorMessage);
                            }

                            @Override
                            public void onNext(Chat chat) {
                                //Dismiss create chat dialog
                                if (createChatDialog.isShowing()) {
                                    createChatDialog.dismiss();
                                }
                                if (chat.isSuccess()) {
                                    //If creating a new chat was successful, then convey this
                                    // information to the ChatListFragment so that it can add
                                    // newly created chat in the chat list
                                    Fragment fragment = pagerAdapter.getFragment(0);
                                    if (fragment != null && fragment instanceof ChatListFragment) {
                                        ((ChatListFragment) fragment).addNewChat(chat.getData());
                                    }
                                } else {
                                    //If creating a new chat failed then notify user
                                    showMessage(createChatErrorMessage);
                                }
                            }
                        });
            } else {
                showMessage(noConnectionMessage);
            }
        }
    }

    @Override
    public void showMessage(String message) {
        Utils.showMessage(this, message);
    }

    @Override
    public void showProfileEditDialog() {
        editProfileDialog.show();
    }

    @Override
    public void hideProfileEditDialog() {
        if (editProfileDialog.isShowing()) {
            editProfileDialog.hide();
        }
    }

    @Override
    public void onChatsLoaded() {
        isChatListLoaded = true;
        animateInFab();
    }

    @Override
    public void onItemClick(ChatList.Data chat) {
        Intent intent = MessageListActivity.getCallingIntent(this, chat.getId(), chat.getName());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //unsubscribe from observable to avoid memory leak
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
