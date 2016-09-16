package com.harryio.orainteractive.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.harryio.orainteractive.ui.account.AccountFragment;
import com.harryio.orainteractive.ui.chat.ChatsFragment;

import java.util.HashMap;
import java.util.Map;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Map<Integer, String> fragmentTagMap;
    private FragmentManager fragmentManager;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentTagMap = new HashMap<>(2);
        fragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();

            case 1:
                return new AccountFragment();
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            fragmentTagMap.put(position, tag);
        }

        return object;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";

            case 1:
                return "Account";
        }

        return "";
    }

    public Fragment getFragment(int position) {
        String tag = fragmentTagMap.get(position);
        if (tag == null) {
            return null;
        }

        return fragmentManager.findFragmentByTag(tag);
    }
}