package com.sarthak.trackit.trackit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarthak.trackit.trackit.R;
import com.sarthak.trackit.trackit.fragments.FriendsFragment;
import com.sarthak.trackit.trackit.fragments.GroupsFragment;
import com.sarthak.trackit.trackit.fragments.MapsFragment;
import com.sarthak.trackit.trackit.model.User;
import com.sarthak.trackit.trackit.services.LocationService;
import com.sarthak.trackit.trackit.utils.Constants;
import com.sarthak.trackit.trackit.utils.UserSharedPreferences;

public class HomeActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    TextView mNavUserName, mNavDisplayName;

    User user = new User();

    int fragmentType = 0;

    BottomNavigationView navigation;
    SearchView searchView;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FloatingActionButton createFriendOrGroupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpToolbar(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        startService(new Intent(HomeActivity.this, LocationService.class));

        /*If the instance state of app onCreate is null,
        MapsFragment is inflated*/
        if (savedInstanceState == null) {
            fragmentInflate(MapsFragment.newInstance());
        }

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        mNavUserName = header.findViewById(R.id.text_nav_username);
        mNavDisplayName = header.findViewById(R.id.text_nav_display_name);
        setUserDetails();

    }

    private void setUserDetails() {

        if (!mUser.isAnonymous()) {
            FirebaseFirestore.getInstance()
                    .collection(Constants.USERS_REFERENCE)
                    .document(mUser.getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        user = task.getResult().toObject(User.class);

                        mNavUserName.setText(user.getUsername());
                        mNavDisplayName.setText(user.getDisplayName());
                    } else {
                        mNavUserName.setText(getText(R.string.error_wait_message));
                        mNavDisplayName.setText(getString(R.string.error_wait_message));
                    }
                }
            });
        } else {
            mNavUserName.setText(getString(R.string.login_request));
            mNavDisplayName.setText(getString(R.string.hi));
        }


        createFriendOrGroupBtn = findViewById(R.id.fab_create_friend_or_group);
        createFriendOrGroupBtn.setOnClickListener(this);
    }

    @Override
    protected int getToolbarID() {
        return R.id.home_activity_toolbar;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(HomeActivity.this, LocationService.class));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        /*if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }*/
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fab_create_friend_or_group:

                switch (fragmentType) {

                    case 1:

                        startActivity(new Intent(HomeActivity.this, CreateGroupActivity.class));
                        break;

                    case 2:

                        Intent searchIntent = new Intent(HomeActivity.this, SearchActivity.class);
                        startActivity(searchIntent);
                        break;
                }
        }
    }

    ;

    /*Used to pass fragment on item selected*/
    public void fragmentInflate(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.home_page_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        //Sets the search icon in the Toolbar
        //Also, its behaviour is defined
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.isSubmitButtonEnabled();
        searchView.animate();
        return true;
    }

    //Toolbar menu options are mentioned here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:

                new UserSharedPreferences(HomeActivity.this).deleteStatus();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(HomeActivity.this, NewUserActivity.class));
                finish();
                break;
            case R.id.action_search:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Mention Navigation Drawer items here
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_camera:
                startActivity(new Intent(this, RequestStatusActivity.class));
                break;

            case R.id.nav_gallery:
                break;

        }
        return true;
    }

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }*/

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentType = 0;
                    selectedFragment = MapsFragment.newInstance();
                    createFriendOrGroupBtn.setVisibility(View.GONE);
                    break;
                case R.id.navigation_groups:
                    fragmentType = 1;
                    selectedFragment = GroupsFragment.newInstance();
                    createFriendOrGroupBtn.setVisibility(View.VISIBLE);
                    break;
                case R.id.navigation_friends:
                    fragmentType = 2;
                    selectedFragment = FriendsFragment.newInstance();
                    createFriendOrGroupBtn.setVisibility(View.VISIBLE);
                    break;
            }
            fragmentInflate(selectedFragment);
            return true;
        }
    };
}
