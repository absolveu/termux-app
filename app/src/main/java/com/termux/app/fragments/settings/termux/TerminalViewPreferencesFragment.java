package com.termux.app.fragments.settings.termux;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

import com.termux.R;
import com.termux.app.TermuxActivity;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import com.termux.shared.termux.settings.properties.TermuxAppSharedProperties;

@Keep
public class TerminalViewPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(TerminalViewPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.termux_terminal_view_preferences, rootKey);

        SeekBarPreference opacityPreference = findPreference("background_overlay_opacity");
        if (opacityPreference != null) {
            opacityPreference.setSummary(getString(
                R.string.termux_terminal_view_background_overlay_opacity_summary,
                opacityPreference.getValue()
            ));

            opacityPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                int value = (Integer) newValue;
                preference.setSummary(getString(
                    R.string.termux_terminal_view_background_overlay_opacity_summary,
                    value
                ));
                return true;
            });
        }

        Preference restorePreference = findPreference("restore_background_overlay_opacity");
        if (restorePreference != null) {
            restorePreference.setOnPreferenceClickListener(preference -> {
                TerminalViewPreferencesDataStore.getInstance(context)
                    .resetBackgroundOverlayOpacity();
                requireActivity().recreate();
                return true;
            });
        }
    }
}

class TerminalViewPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TermuxAppSharedPreferences mPreferences;

    private static TerminalViewPreferencesDataStore mInstance;

    private TerminalViewPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }

    public static synchronized TerminalViewPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TerminalViewPreferencesDataStore(context);
        }
        return mInstance;
    }

    public void resetBackgroundOverlayOpacity() {
        if (mPreferences == null) return;

        mPreferences.setBackgroundOverlayOpacity(-1);
        TermuxActivity.updateTermuxActivityStyling(mContext, false);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        if (mPreferences == null || key == null) return;

        switch (key) {
            case "terminal_margin_adjustment":
                mPreferences.setTerminalMarginAdjustment(value);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (mPreferences == null) return defValue;

        switch (key) {
            case "terminal_margin_adjustment":
                return mPreferences.isTerminalMarginAdjustmentEnabled();
            default:
                return defValue;
        }
    }

    @Override
    public void putInt(String key, int value) {
        if (mPreferences == null || key == null) return;

        if ("background_overlay_opacity".equals(key)) {
            mPreferences.setBackgroundOverlayOpacity(Math.max(0, Math.min(100, value)));
            TermuxActivity.updateTermuxActivityStyling(mContext, false);
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        if (mPreferences == null || key == null) return defValue;

        if ("background_overlay_opacity".equals(key)) {
            int value = mPreferences.getBackgroundOverlayOpacity();
            if (value >= 0) return Math.min(100, value);

            TermuxAppSharedProperties properties = TermuxAppSharedProperties.getProperties();
            if (properties == null) return defValue;

            int alpha = (properties.getBackgroundOverlayColor() >>> 24) & 0xFF;
            return (alpha * 100 + 127) / 255;
        }

        return defValue;
    }
}
