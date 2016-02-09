package com.gh4a.activities.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.gh4a.Constants;
import com.gh4a.R;
import com.gh4a.fragment.RepositoryListContainerFragment;

public class RepositoryFactory extends FragmentFactory {
    private static final int[] TAB_TITLES = new int[] {
        R.string.my_repositories
    };

    private String mUserLogin;
    private RepositoryListContainerFragment.FilterDrawerHelper mFilterDrawerHelper;
    private RepositoryListContainerFragment.SortDrawerHelper mSortDrawerHelper;
    private RepositoryListContainerFragment mFragment;

    private static final String STATE_KEY_FRAGMENT = "repoFactoryFragment";

    public RepositoryFactory(HomeActivity activity, String userLogin) {
        super(activity);
        mUserLogin = userLogin;

        mFilterDrawerHelper = RepositoryListContainerFragment.FilterDrawerHelper.create(mUserLogin,
                Constants.User.TYPE_USER);
        mSortDrawerHelper = new RepositoryListContainerFragment.SortDrawerHelper();
    }

    @Override
    protected int getTitleResId() {
        return R.string.my_repositories;
    }

    @Override
    protected int[] getTabTitleResIds() {
        return TAB_TITLES;
    }

    @Override
    protected int[] getToolDrawerMenuResIds() {
        int sortMenuResId = mSortDrawerHelper.getMenuResId();
        int filterMenuResId = mFilterDrawerHelper.getMenuResId();
        if (sortMenuResId == 0) {
            return new int[] { filterMenuResId };
        } else {
            return new int[] { sortMenuResId, filterMenuResId };
        }
    }

    @Override
    protected void prepareToolDrawerMenu(Menu menu) {
        super.prepareToolDrawerMenu(menu);
        if (mFragment != null) {
            mFilterDrawerHelper.selectFilterType(menu, mFragment.getFilterType());
            mSortDrawerHelper.selectSortType(menu,
                    mFragment.getSortOrder(), mFragment.getSortDirection());
        }
    }

    @Override
    protected boolean onDrawerItemSelected(MenuItem item) {
        String type = mFilterDrawerHelper.handleSelectionAndGetFilterType(item);
        if (type != null) {
            mFragment.setFilterType(type);
            mSortDrawerHelper.setFilterType(type);
            mActivity.doInvalidateOptionsMenuAndToolDrawer();
            return true;
        }
        String[] sortOrderAndDirection = mSortDrawerHelper.handleSelectionAndGetSortOrder(item);
        if (sortOrderAndDirection != null) {
            mFragment.setSortOrder(sortOrderAndDirection[0], sortOrderAndDirection[1]);
            mActivity.doInvalidateOptionsMenuAndToolDrawer();
            return true;
        }
        return super.onDrawerItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFragment != null) {
            mActivity.getSupportFragmentManager().putFragment(outState, STATE_KEY_FRAGMENT, mFragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFragment.destroyChildren();
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        mFragment = (RepositoryListContainerFragment)
                mActivity.getSupportFragmentManager().getFragment(state, STATE_KEY_FRAGMENT);
        if (mFragment != null) {
            mSortDrawerHelper.setFilterType(mFragment.getFilterType());
            mActivity.doInvalidateOptionsMenuAndToolDrawer();
        }
    }

    @Override
    protected Fragment getFragment(int position) {
        mFragment = RepositoryListContainerFragment.newInstance(mUserLogin,
                Constants.User.TYPE_USER);
        mActivity.doInvalidateOptionsMenuAndToolDrawer();
        return mFragment;
    }

    @Override
    protected void onRefreshFragment(Fragment fragment) {
        ((RepositoryListContainerFragment) fragment).refresh();
    }
}
