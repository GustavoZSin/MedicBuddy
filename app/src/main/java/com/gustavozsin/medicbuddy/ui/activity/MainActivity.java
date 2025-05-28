package com.gustavozsin.medicbuddy.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configureFabNewMedicineScheduling();

        configureDrawer();
        configureNavigation();
    }

    private void configureNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationView navigationView = binding.navView;
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void configureDrawer() {
        DrawerLayout drawer = binding.drawerLayout;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_my_reminders, R.id.nav_my_pharmacy)
                .setOpenableLayout(drawer)
                .build();
    }

    private void configureFabNewMedicineScheduling() {
        setSupportActionBar(binding.appBarMain.toolbar);

        binding.appBarMain.appBarMainFabNewMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFormMedicineSchedulingActivity();
            }
        });
    }

    private void openFormMedicineSchedulingActivity() {
        startActivity(new Intent(MainActivity.this, FormMedicineSchedulingActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}