package com.gh4a.activities.home;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import org.eclipse.egit.github.core.User;

public abstract class FragmentFactory {
    protected final HomeActivity mActivity;

    protected FragmentFactory(HomeActivity activity) {
        mActivity = activity;
    }

    protected abstract @StringRes int getTitleResId();
    protected abstract int[] getTabTitleResIds();
    protected abstract Fragment makeFragment(int position);

    protected void onFragmentInstantiated(Fragment f, int position) {
    }

    protected void onFragmentDestroyed(Fragment f) {
    }

    protected int[] getHeaderColorAttrs() {
        return null;
    }

    protected int[] getToolDrawerMenuResIds() {
        return null;
    }

    protected void prepareToolDrawerMenu(Menu menu) {

    }

    protected boolean onDrawerItemSelected(MenuItem item) {
        return false;
    }

    protected boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    protected boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    protected void onSaveInstanceState(Bundle outState) {}

    protected void onRestoreInstanceState(Bundle state) {}

    protected void onRefresh() {}

    protected void onDestroy() {}

    protected @IdRes int getInitialToolDrawerSelection() {
        return 0;
    }

    protected void setUserInfo(User user) { }
}
