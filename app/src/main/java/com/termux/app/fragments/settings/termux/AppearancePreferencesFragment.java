package com.termux.app.fragments.settings.termux;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.termux.R;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

@Keep
public class AppearancePreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(
            AppearancePreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.termux_appearance_preferences, rootKey);
    }
}

class AppearancePreferencesDataStore extends PreferenceDataStore {

    private final TermuxAppSharedPreferences mPreferences;
    private static AppearancePreferencesDataStore mInstance;

    private AppearancePreferencesDataStore(Context context) {
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }

    public static synchronized AppearancePreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppearancePreferencesDataStore(context);
        }
        return mInstance;
    }

    @Override
    public void putBoolean(String key, boolean value) {
        if (mPreferences == null || key == null) return;

        if ("monet_background_enabled".equals(key)) {
            mPreferences.setMonetBackgroundEnabled(value);
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (mPreferences == null || key == null) return defValue;

        if ("monet_background_enabled".equals(key)) {
            return mPreferences.isMonetBackgroundEnabled();
        }

        return defValue;
    }
}
