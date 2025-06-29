package com.gustavozsin.medicbuddy.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.gustavozsin.medicbuddy.R;
import com.gustavozsin.medicbuddy.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLanguageFromLastSession();

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Padroniza as cores do app bar e do FAB
        binding.appBarMain.toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
        binding.appBarMain.appBarMainFabNewMedicine.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary))
        );
        binding.appBarMain.appBarMainFabNewMedicine.setColorFilter(
                getResources().getColor(R.color.onPrimary)
        );

        configureFabNewMedicineScheduling();
        configureDrawer();
        configureNavigation();
    }

    private void getLanguageFromLastSession() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("app_lang", "pt");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_language) {
            showLanguageDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLanguageDialog() {
        String[] languages = getResources().getStringArray(R.array.languages);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MedicBuddy_LightDialog);
        builder.setTitle(getResources().getString(R.string.choose_language))
            .setItems(languages, (dialog, which) -> {
                if (which == 0) {
                    setLocale("pt");
                } else {
                    setLocale("en");
                }
            });
        AlertDialog dialog = builder.create();
        dialog.show();

        // Aplica cor azul claro ao texto dos itens e título
        int blue = getResources().getColor(R.color.lightBlueText, getTheme());
        int titleId = getResources().getIdentifier("alertTitle", "id", "android");
        if (titleId > 0) {
            TextView title = dialog.findViewById(titleId);
            if (title != null) title.setTextColor(blue);
        }
        ListView listView = dialog.getListView();
        if (listView != null) {
            for (int i = 0; i < listView.getChildCount(); i++) {
                View v = listView.getChildAt(i);
                if (v instanceof TextView) {
                    ((TextView) v).setTextColor(blue);
                }
            }
            listView.setAdapter(new android.widget.ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, languages) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    if (v instanceof TextView) {
                        ((TextView) v).setTextColor(blue);
                    }
                    return v;
                }
            });
        }
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        saveLanguageForNextSession(lang);

        recreate();
    }

    private void saveLanguageForNextSession(String lang) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("app_lang", lang);
        editor.apply();
    }
}