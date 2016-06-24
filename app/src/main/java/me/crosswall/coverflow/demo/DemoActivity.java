package me.crosswall.coverflow.demo;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


/**
 * Created by yuweichen on 16/4/30.
 */
public class DemoActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.demo_list);

        PreferenceManager manager = getPreferenceManager();

        Preference nomarl = manager.findPreference("normal");
        nomarl.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        String key = preference.getKey();
        Intent intent = new Intent();
        switch (key){
            case "normal":
                intent.setClass(DemoActivity.this,NormalActivity.class);

                break;
        }

        startActivity(intent);
        return false;
    }
}
