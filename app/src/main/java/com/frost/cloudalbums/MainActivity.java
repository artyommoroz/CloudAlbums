package com.frost.cloudalbums;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.frost.cloudalbums.util.PreferenceHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AlbumsFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener,
        TracksFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.spinner)
    Spinner spinner;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    private static final String TAG_ALBUMS = "albums";
    private static final String TAG_PROFILE = "profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24px);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_profile, 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24px);
            toolbar.setTitle(null);
            spinner.setVisibility(View.VISIBLE);
            resetUpBehavior();
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        String tag = null;
        switch (item.getItemId()) {
            case R.id.nav_profile:
                fragment = PreferenceHelper.isUserLoggedIn(this) ? ProfileFragment.newInstance()
                        : LoginFragment.newInstance();
                tag = TAG_PROFILE;
                toolbar.setTitle(getString(R.string.navigation_profile));
                break;
            case R.id.nav_albums:
                fragment = AlbumsFragment.newInstance();
                tag = TAG_ALBUMS;
                toolbar.setTitle(null);
                break;
        }
        resetUpBehavior();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if ((fragmentManager.findFragmentByTag(tag) == null)) {
            fragmentManager.beginTransaction().replace(R.id.content, fragment, tag).commit();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClickAlbum(int albumId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, TracksFragment.newInstance(albumId))
                .hide(fragmentManager.findFragmentByTag(TAG_ALBUMS))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSearchItemPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.content, SearchFragment.newInstance())
                .hide(fragmentManager.findFragmentByTag(TAG_ALBUMS))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onLoginButtonPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, ProfileFragment.newInstance())
                .commit();
    }


    @Override
    public void onBackPressedTracks() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24px);
        toolbar.setTitle(null);
        spinner.setVisibility(View.VISIBLE);
        resetUpBehavior();
    }

    @Override
    public void onBackPressedSearch() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24px);
        toolbar.setTitle(null);
        spinner.setVisibility(View.VISIBLE);
        resetUpBehavior();
    }

    private void resetUpBehavior() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

}
