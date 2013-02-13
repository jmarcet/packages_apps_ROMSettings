package com.aokp.romcontrol.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Spannable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.util.CMDProcessor;
import com.aokp.romcontrol.util.AbstractAsyncSuCMDProcessor;
import com.aokp.romcontrol.util.Helpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.StringBuilder;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class UserInterface extends AOKPPreferenceFragment implements OnPreferenceChangeListener {

    public static final String TAG = "UserInterface";

    private static final String PREF_180 = "rotate_180";
    private static final String PREF_STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String PREF_CUSTOM_CARRIER_LABEL = "custom_carrier_label";
	private static final String PREF_SHOW_OVERFLOW = "show_overflow";
    private static final String PREF_VIBRATE_NOTIF_EXPAND = "vibrate_notif_expand";
    private static final String PREF_RECENT_KILL_ALL = "recent_kill_all";
    private static final String PREF_RAM_USAGE_BAR = "ram_usage_bar";
	private static final String PREF_IME_SWITCHER = "ime_switcher";
	private static final String PREF_STATUSBAR_BRIGHTNESS = "statusbar_brightness_slider";
	private static final String PREF_USER_MODE_UI = "user_mode_ui";
	private static final String PREF_FORCE_DUAL_PANEL = "force_dualpanel";
	private static final String PREF_HIDE_EXTRAS = "hide_extras";
	private static final String PIE_CONTROLS = "pie_controls";
    private static final String PIE_GRAVITY = "pie_gravity";
    private static final String PIE_MODE = "pie_mode";
    private static final String PIE_SIZE = "pie_size";
    private static final String PIE_TRIGGER = "pie_trigger";
    private static final String PIE_GAP = "pie_gap";
    private static final String PIE_NOTIFICATIONS = "pie_notifications";
    private static final String PIE_MENU = "pie_menu";
    private static final String PIE_SEARCH = "pie_search";
    private static final String PREF_WAKEUP_WHEN_PLUGGED_UNPLUGGED = "wakeup_when_plugged_unplugged";
    private static final String PREF_POWER_CRT_SCREEN_ON = "system_power_crt_screen_on";
    private static final String PREF_POWER_CRT_SCREEN_OFF = "system_power_crt_screen_off";

    private static final int REQUEST_PICK_WALLPAPER = 201;
    private static final int REQUEST_PICK_CUSTOM_ICON = 202;
    private static final int REQUEST_PICK_BOOT_ANIMATION = 203;
    private static final int SELECT_ACTIVITY = 4;
    private static final int SELECT_WALLPAPER = 5;

    private static final String WALLPAPER_NAME = "notification_wallpaper.jpg";

    CheckBoxPreference mAllow180Rotation;
    CheckBoxPreference mDisableBootAnimation;
    CheckBoxPreference mStatusBarNotifCount;
    Preference mCustomLabel;
    Preference mCustomBootAnimation;
    ImageView view;
    TextView error;
	CheckBoxPreference mShowActionOverflow;
    CheckBoxPreference mVibrateOnExpand;
    CheckBoxPreference mRecentKillAll;
    CheckBoxPreference mRamBar;
	CheckBoxPreference mShowImeSwitcher;
	CheckBoxPreference mStatusbarSliderPreference;
	ListPreference mUserModeUI;
	CheckBoxPreference mDualpane;
	Preference mLcdDensity;
	CheckBoxPreference mHideExtras;
	ListPreference mPieMode;
    ListPreference mPieSize;
    ListPreference mPieGravity;
    CheckBoxPreference mPieControls;
    ListPreference mPieTrigger;
    ListPreference mPieGap;
    CheckBoxPreference mPieNotifi;
    CheckBoxPreference mPieMenu;
    CheckBoxPreference mPieSearch;
    CheckBoxPreference mWakeUpWhenPluggedOrUnplugged;
    CheckBoxPreference mCrtOff;
    CheckBoxPreference mCrtOn;

    private AnimationDrawable mAnimationPart1;
    private AnimationDrawable mAnimationPart2;
    private String mPartName1;
    private String mPartName2;
    private int delay;
    private int height;
    private int width;
    private String errormsg;
    private String bootAniPath;

    private Random randomGenerator = new Random();
    // previous random; so we don't repeat
    private static int mLastRandomInsultIndex = -1;
    private String[] mInsults;
    
    int newDensityValue;
    DensityChanger densityFragment;

    private int seekbarProgress;
    private int mAllowedLocations;
    String mCustomLabelText = null;
    
    private boolean isCrtOffChecked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_ui);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_ui);

        PreferenceScreen prefs = getPreferenceScreen();
		ContentResolver cr = mContext.getContentResolver();
        mInsults = mContext.getResources().getStringArray(
                R.array.disable_bootanimation_insults);

        mAllow180Rotation = (CheckBoxPreference) findPreference(PREF_180);
        mAllow180Rotation.setChecked(Settings.System.getInt(cr,
                Settings.System.ACCELEROMETER_ROTATION_ANGLES, (1 | 2 | 8)) == (1 | 2 | 4 | 8));

        mStatusBarNotifCount = (CheckBoxPreference) findPreference(PREF_STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked(Settings.System.getBoolean(cr,
                Settings.System.STATUSBAR_NOTIF_COUNT, false));

        mDisableBootAnimation = (CheckBoxPreference)findPreference("disable_bootanimation");
        mDisableBootAnimation.setChecked(!new File("/system/media/bootanimation.zip").exists());
        if (mDisableBootAnimation.isChecked()) {
            Resources res = mContext.getResources();
            String[] insults = res.getStringArray(R.array.disable_bootanimation_insults);
            int randomInt = randomGenerator.nextInt(insults.length);
            mDisableBootAnimation.setSummary(insults[randomInt]);
        }

        mCustomBootAnimation = findPreference("custom_bootanimation");

        mCustomLabel = findPreference(PREF_CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();

        mVibrateOnExpand = (CheckBoxPreference) findPreference(PREF_VIBRATE_NOTIF_EXPAND);
        mVibrateOnExpand.setChecked(Settings.System.getBoolean(cr,
                Settings.System.VIBRATE_NOTIF_EXPAND, true));
        if (!hasVibration) {
            ((PreferenceGroup)findPreference("notification")).removePreference(mVibrateOnExpand);
        }

        mRecentKillAll = (CheckBoxPreference) findPreference(PREF_RECENT_KILL_ALL);
        mRecentKillAll.setChecked(Settings.System.getBoolean(cr,
                Settings.System.RECENT_KILL_ALL_BUTTON, false));

        mRamBar = (CheckBoxPreference) findPreference(PREF_RAM_USAGE_BAR);
        mRamBar.setChecked(Settings.System.getBoolean(cr,
                Settings.System.RAM_USAGE_BAR, false));

		mShowActionOverflow = (CheckBoxPreference) findPreference(PREF_SHOW_OVERFLOW);
        mShowActionOverflow.setChecked((Settings.System.getInt(getActivity().
                        getApplicationContext().getContentResolver(),
                        Settings.System.UI_FORCE_OVERFLOW_BUTTON, 0) == 1));

		mShowImeSwitcher = (CheckBoxPreference) findPreference(PREF_IME_SWITCHER);
        mShowImeSwitcher.setChecked(Settings.System.getBoolean(cr,
                Settings.System.SHOW_STATUSBAR_IME_SWITCHER, true));

		mStatusbarSliderPreference = (CheckBoxPreference) findPreference(PREF_STATUSBAR_BRIGHTNESS);
        mStatusbarSliderPreference.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                Settings.System.STATUSBAR_BRIGHTNESS_SLIDER, true));

		mUserModeUI = (ListPreference) findPreference(PREF_USER_MODE_UI);
        int uiMode = Settings.System.getInt(cr,
                Settings.System.CURRENT_UI_MODE, 0);
        mUserModeUI.setValue(Integer.toString(Settings.System.getInt(cr,
                Settings.System.USER_UI_MODE, uiMode)));
        mUserModeUI.setOnPreferenceChangeListener(this);
        
        mHideExtras = (CheckBoxPreference) findPreference(PREF_HIDE_EXTRAS);
        mHideExtras.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
        		Settings.System.HIDE_EXTRAS_SYSTEM_BAR, false));

		mDualpane = (CheckBoxPreference) findPreference(PREF_FORCE_DUAL_PANEL);
        mDualpane.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                        Settings.System.FORCE_DUAL_PANEL, getResources().getBoolean(
                        com.android.internal.R.bool.preferences_prefer_dual_pane)));
                        
        mPieControls = (CheckBoxPreference) findPreference(PIE_CONTROLS);
        mPieControls.setChecked((Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_CONTROLS, 0) == 1));

        mPieGravity = (ListPreference) findPreference(PIE_GRAVITY);
        int pieGravity = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_GRAVITY, 3);
        mPieGravity.setValue(String.valueOf(pieGravity));
        mPieGravity.setOnPreferenceChangeListener(this);

        mPieMode = (ListPreference) findPreference(PIE_MODE);
        int pieMode = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_MODE, 2);
        mPieMode.setValue(String.valueOf(pieMode));
        mPieMode.setOnPreferenceChangeListener(this);

		mPieSize = (ListPreference) findPreference(PIE_SIZE);
        mPieTrigger = (ListPreference) findPreference(PIE_TRIGGER);
        try {
            float pieSize = Settings.System.getFloat(mContext.getContentResolver(),
                    Settings.System.PIE_SIZE);
            mPieSize.setValue(String.valueOf(pieSize));
  
            float pieTrigger = Settings.System.getFloat(mContext.getContentResolver(),
                    Settings.System.PIE_TRIGGER);
            mPieTrigger.setValue(String.valueOf(pieTrigger));
        } catch(Settings.SettingNotFoundException ex) {
            // So what
        }

        mPieSize.setOnPreferenceChangeListener(this);
        mPieTrigger.setOnPreferenceChangeListener(this);

        mPieGap = (ListPreference) findPreference(PIE_GAP);
        int pieGap = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_GAP, 1);
        mPieGap.setValue(String.valueOf(pieGap));
        mPieGap.setOnPreferenceChangeListener(this);

        mPieNotifi = (CheckBoxPreference) findPreference(PIE_NOTIFICATIONS);
        mPieNotifi.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.PIE_NOTIFICATIONS, 0) == 1));
                
        mPieMenu = (CheckBoxPreference) findPreference(PIE_MENU);
        mPieMenu.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_MENU, 0) == 1);

        mPieSearch = (CheckBoxPreference) findPreference(PIE_SEARCH);
        mPieSearch.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.PIE_SEARCH, 1) == 1);
                
        mWakeUpWhenPluggedOrUnplugged = (CheckBoxPreference) findPreference(PREF_WAKEUP_WHEN_PLUGGED_UNPLUGGED);
        mWakeUpWhenPluggedOrUnplugged.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                        Settings.System.WAKEUP_WHEN_PLUGGED_UNPLUGGED, true));

        // hide option if device is already set to never wake up
        if(!mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_unplugTurnsOnScreen)) {
            ((PreferenceGroup) findPreference("misc")).removePreference(mWakeUpWhenPluggedOrUnplugged);
        }
                        
        mLcdDensity = findPreference("lcd_density_setup");
        String currentProperty = SystemProperties.get("ro.sf.lcd_density");
        try {
            newDensityValue = Integer.parseInt(currentProperty);
        } catch (Exception e) {
            getPreferenceScreen().removePreference(mLcdDensity);
        }

        mLcdDensity.setSummary(getResources().getString(R.string.current_lcd_density) + currentProperty);
        
        // respect device default configuration
        // true fades while false animates
        boolean electronBeamFadesConfig = mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_animateScreenLights);

        // use this to enable/disable crt on feature
        // crt only works if crt off is enabled
        // total system failure if only crt on is enabled
        isCrtOffChecked = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SYSTEM_POWER_ENABLE_CRT_OFF,
                electronBeamFadesConfig ? 0 : 1) == 1;

        mCrtOff = (CheckBoxPreference) findPreference(PREF_POWER_CRT_SCREEN_OFF);
        mCrtOff.setChecked(isCrtOffChecked);
        mCrtOff.setOnPreferenceChangeListener(this);

        mCrtOn = (CheckBoxPreference) findPreference(PREF_POWER_CRT_SCREEN_ON);
        mCrtOn.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SYSTEM_POWER_ENABLE_CRT_ON, 0) == 1);
        mCrtOn.setEnabled(isCrtOffChecked);
        mCrtOn.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
        checkControls();
    }
    
    private void checkControls() {
        boolean pieCheck = mPieControls.isChecked();
        mPieGravity.setEnabled(pieCheck);
        mPieMode.setEnabled(pieCheck);
        mPieSize.setEnabled(pieCheck);
        mPieTrigger.setEnabled(pieCheck);
        mPieGap.setEnabled(pieCheck);
        mPieNotifi.setEnabled(pieCheck);
    }

    private void updateCustomLabelTextSummary() {
        mCustomLabelText = Settings.System.getString(getActivity().getContentResolver(),
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomLabelText == null || mCustomLabelText.length() == 0) {
            mCustomLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomLabel.setSummary(mCustomLabelText);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            final Preference preference) {
        if (preference == mAllow180Rotation) {
            boolean checked = ((CheckBoxPreference) preference).isChecked();
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION_ANGLES, checked ? (1 | 2 | 4 | 8) : (1 | 2 | 8 ));
            return true;
        } else if (preference == mStatusBarNotifCount) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.STATUSBAR_NOTIF_COUNT,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mDisableBootAnimation) {
            DisableBootAnimation();
            return true;
        } else if (preference == mHideExtras) {
        	Settings.System.putBoolean(mContext.getContentResolver(),
        			Settings.System.HIDE_EXTRAS_SYSTEM_BAR,
        			((CheckBoxPreference) preference).isChecked());
		} else if (preference == mShowActionOverflow) {	
            boolean enabled = mShowActionOverflow.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.UI_FORCE_OVERFLOW_BUTTON,
                    enabled ? 1 : 0);
            // Show toast appropriately
            if (enabled) {
                Toast.makeText(getActivity(), R.string.show_overflow_toast_enable,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), R.string.show_overflow_toast_disable,
                        Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (preference == mCustomBootAnimation) {
            PackageManager packageManager = getActivity().getPackageManager();
            Intent test = new Intent(Intent.ACTION_GET_CONTENT);
            test.setType("file/*");
            List<ResolveInfo> list = packageManager.queryIntentActivities(test, PackageManager.GET_ACTIVITIES);
            if(list.size() > 0) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                intent.setType("file/*");
                startActivityForResult(intent, REQUEST_PICK_BOOT_ANIMATION);
            } else {
                //No app installed to handle the intent - file explorer required
                Toast.makeText(mContext, R.string.install_file_manager_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (preference == mCustomLabel) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(mCustomLabelText != null ? mCustomLabelText : "");
            alert.setView(input);

            alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = ((Spannable) input.getText()).toString();
                    Settings.System.putString(getActivity().getContentResolver(),
                            Settings.System.CUSTOM_CARRIER_LABEL, value);
                    updateCustomLabelTextSummary();
                    Intent i = new Intent();
                    i.setAction("com.aokp.romcontrol.LABEL_CHANGED");
                    mContext.sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
        } else if (preference == mVibrateOnExpand) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.VIBRATE_NOTIF_EXPAND,
                    ((CheckBoxPreference) preference).isChecked());
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mRecentKillAll) {
            boolean checked = ((CheckBoxPreference)preference).isChecked();
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.RECENT_KILL_ALL_BUTTON, checked ? true : false);
            return true;
		} else if (preference == mShowImeSwitcher) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.SHOW_STATUSBAR_IME_SWITCHER,
                    isCheckBoxPrefernceChecked(preference));
            return true;
		} else if (preference == mStatusbarSliderPreference) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BRIGHTNESS_SLIDER,
                    isCheckBoxPrefernceChecked(preference));
            return true;
		} else if (preference == mDualpane) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.FORCE_DUAL_PANEL,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLcdDensity) {
            ((PreferenceActivity) getActivity())
                    .startPreferenceFragment(new DensityChanger(), true);
            return true;
        } else if (preference == mRamBar) {
            boolean checked = ((CheckBoxPreference)preference).isChecked();
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.RAM_USAGE_BAR, checked ? true : false);
            return true;
        } else if (preference == mWakeUpWhenPluggedOrUnplugged) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.WAKEUP_WHEN_PLUGGED_UNPLUGGED,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mPieControls) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.PIE_CONTROLS,
                    mPieControls.isChecked() ? 1 : 0);
            checkControls();
            Helpers.restartSystemUI();
        } else if (preference == mPieNotifi) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.PIE_NOTIFICATIONS,
                    mPieNotifi.isChecked() ? 1 : 0);
        } else if (preference == mPieMenu) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_MENU, mPieMenu.isChecked() ? 1 : 0);
        } else if (preference == mPieSearch) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_SEARCH, mPieSearch.isChecked() ? 1 : 0);
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

	@Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mUserModeUI) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.USER_UI_MODE, Integer.parseInt((String) newValue));
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mPieMode) {
            int pieMode = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_MODE, pieMode);
            return true;
        } else if (preference == mPieSize) {
            float pieSize = Float.valueOf((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.PIE_SIZE, pieSize);
            return true;
        } else if (preference == mPieGravity) {
            int pieGravity = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_GRAVITY, pieGravity);
            return true;
        } else if (preference == mPieGap) {
            int pieGap = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_GAP, pieGap);
            return true;
        } else if (preference == mPieTrigger) {
            float pieTrigger = Float.valueOf((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.PIE_TRIGGER, pieTrigger);
            return true;
        } else if (mCrtOff.equals(preference)) {
            isCrtOffChecked = ((Boolean) newValue).booleanValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SYSTEM_POWER_ENABLE_CRT_OFF,
                    (isCrtOffChecked ? 1 : 0));
            // if crt off gets turned off, crt on gets turned off and disabled
            if (!isCrtOffChecked) {
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.SYSTEM_POWER_ENABLE_CRT_ON, 0);
                mCrtOn.setChecked(false);
            }
            mCrtOn.setEnabled(isCrtOffChecked);
            return true;
        } else if (mCrtOn.equals(preference)) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SYSTEM_POWER_ENABLE_CRT_ON,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_interface, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.remove_wallpaper:
                File f = new File(mContext.getFilesDir(), WALLPAPER_NAME);
                mContext.deleteFile(WALLPAPER_NAME);
                Helpers.restartSystemUI();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private Uri getNotificationExternalUri() {
        File dir = mContext.getExternalCacheDir();
        File wallpaper = new File(dir, WALLPAPER_NAME);

        return Uri.fromFile(wallpaper);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_WALLPAPER) {

                FileOutputStream wallpaperStream = null;
                try {
                    wallpaperStream = mContext.openFileOutput(WALLPAPER_NAME,
                            Context.MODE_WORLD_READABLE);
                } catch (FileNotFoundException e) {
                    return; // NOOOOO
                }

                Uri selectedImageUri = getNotificationExternalUri();
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath());

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, wallpaperStream);
                Helpers.restartSystemUI();
            } else if (requestCode == REQUEST_PICK_BOOT_ANIMATION) {
                if (data==null) {
                    //Nothing returned by user, probably pressed back button in file manager
                    return;
                }

                bootAniPath = data.getData().getEncodedPath();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.bootanimation_preview);
                builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Helpers.getMount("rw");
                        //backup old boot animation
                        new CMDProcessor().su.runWaitFor("mv /system/media/bootanimation.zip /system/media/bootanimation.backup");

                        //Copy new bootanimation, give proper permissions
                        new CMDProcessor().su.runWaitFor("cp "+ bootAniPath +" /system/media/bootanimation.zip");
                        new CMDProcessor().su.runWaitFor("chmod 644 /system/media/bootanimation.zip");

                        //Update setting to reflect that boot animation is now enabled
                        mDisableBootAnimation.setChecked(false);
			DisableBootAnimation();
                        Helpers.getMount("ro");

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(com.android.internal.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.dialog_bootanimation_preview,
                        (ViewGroup) getActivity().findViewById(R.id.bootanimation_layout_root));

                error = (TextView) layout.findViewById(R.id.textViewError);

                view = (ImageView) layout.findViewById(R.id.imageViewPreview);
                view.setVisibility(View.GONE);

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                view.setLayoutParams(new LinearLayout.LayoutParams(size.x/2, size.y/2));

                error.setText(R.string.creating_preview);

                builder.setView(layout);

                AlertDialog dialog = builder.create();

                dialog.setOwnerActivity(getActivity());

                dialog.show();

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        createPreview(bootAniPath);
                    }
                });
                thread.start();

            }
        }
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private void createPreview(String path) {
        File zip = new File(path);

        ZipFile zipfile = null;
        String desc = "";
        try {
            zipfile = new ZipFile(zip);
            ZipEntry ze = zipfile.getEntry("desc.txt");
            InputStream in = zipfile.getInputStream(ze);
            InputStreamReader is = new InputStreamReader(in);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();
            while(read != null) {
                sb.append(read);
                sb.append("\n");
                read = br.readLine();
            }
            desc = sb.toString();
            br.close();
            is.close();
            in.close();

        } catch (Exception e1) {
            errormsg = getActivity().getString(R.string.error_reading_zip_file);
            errorHandler.sendEmptyMessage(0);
            return;
        }

        String[] info = desc.replace("\\r", "").split("\\n");

        width = Integer.parseInt(info[0].split(" ")[0]);
        height = Integer.parseInt(info[0].split(" ")[1]);
        delay = Integer.parseInt(info[0].split(" ")[2]);

        mPartName1 = info[1].split(" ")[3];

        try {
            if (info.length > 2) {
                mPartName2 = info[2].split(" ")[3];
            }
            else {
                mPartName2 = "";
            }
        }
        catch (Exception e)
        {
            mPartName2 = "";
        }

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 4;

        mAnimationPart1 = new AnimationDrawable();
        mAnimationPart2 = new AnimationDrawable();

        try
        {
            for (Enumeration<? extends ZipEntry> e = zipfile.entries(); e.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String partname = entry.getName().split("/")[0];
                if (mPartName1.equalsIgnoreCase(partname)) {
                    InputStream is = zipfile.getInputStream(entry);
                    mAnimationPart1.addFrame(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(is, null, opt)), delay);
                    is.close();
                }
                else if (mPartName2.equalsIgnoreCase(partname)) {
                    InputStream is = zipfile.getInputStream(entry);
                    mAnimationPart2.addFrame(new BitmapDrawable(getResources(), BitmapFactory.decodeStream(is, null, opt)), delay);
                    is.close();
                }
            }
        } catch (IOException e1) {
            errormsg = getActivity().getString(R.string.error_creating_preview);
            errorHandler.sendEmptyMessage(0);
            return;
        }

        if (mPartName2.length() > 0) {
            Log.d(TAG, "Multipart Animation");
            mAnimationPart1.setOneShot(false);
            mAnimationPart2.setOneShot(false);

            mAnimationPart1.setOnAnimationFinishedListener(new AnimationDrawable.OnAnimationFinishedListener() {

                @Override
                public void onAnimationFinished() {
                    Log.d(TAG, "First part finished");
                    view.setImageDrawable(mAnimationPart2);
                    mAnimationPart1.stop();
                    mAnimationPart2.start();
                }
            });

        }
        else {
            mAnimationPart1.setOneShot(false);
        }

        finishedHandler.sendEmptyMessage(0);

    }

    /**
     * creates a couple commands to perform all root
     * operations needed to disable/enable bootanimations
     *
     * @param checked state of CheckBox
     * @return script to turn bootanimations on/off
     */
    private String[] getBootAnimationCommand(boolean checked) {
        String[] cmds = new String[2];
        String storedLocation = "/system/media/bootanimation.backup";
        String activeLocation = "/system/media/bootanimation.zip";
        if (checked) {
            /* make backup */
            cmds[0] = "mv " + activeLocation + " " + storedLocation + "; ";
        } else {
            /* apply backup */
            cmds[0] = "mv " + storedLocation + " " + activeLocation + "; ";
        }
        /*
         * use sed to replace build.prop property
         * debug.sf.nobootanimation=[1|0]
         *
         * without we get the Android shine animation when
         * /system/media/bootanimation.zip is not found
         */
        cmds[1] = "busybox sed -i \"/debug.sf.nobootanimation/ c "
                + "debug.sf.nobootanimation=" + String.valueOf(checked ? 1 : 0)
                + "\" " + "/system/build.prop";
        return cmds;
    }

    private Handler errorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            view.setVisibility(View.GONE);
            error.setText(errormsg);
        }
    };
    
    private void DisableBootAnimation() {
        CMDProcessor term = new CMDProcessor();
        if (!term.su.runWaitFor(
                "grep -q \"debug.sf.nobootanimation\" /system/build.prop")
                .success()) {
            // if not add value
            Helpers.getMount("rw");
            term.su.runWaitFor("echo debug.sf.nobootanimation="
                + String.valueOf(mDisableBootAnimation.isChecked() ? 1 : 0)
                + " >> /system/build.prop");
            Helpers.getMount("ro");
        }
        // preform bootanimation operations off UI thread
        AbstractAsyncSuCMDProcessor processor = new AbstractAsyncSuCMDProcessor(true) {
            @Override
            protected void onPostExecute(String result) {
                if (mDisableBootAnimation.isChecked()) {
                    // do not show same insult as last time
                    int newInsult = randomGenerator.nextInt(mInsults.length);
                    while (newInsult == mLastRandomInsultIndex)
                        newInsult = randomGenerator.nextInt(mInsults.length);

                    // update our static index reference
                    mLastRandomInsultIndex = newInsult;
                    mDisableBootAnimation.setSummary(mInsults[newInsult]);
                } else {
                    mDisableBootAnimation.setSummary("");
                }
            }
        };
        processor.execute(getBootAnimationCommand(mDisableBootAnimation.isChecked()));
    }

    private Handler finishedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            view.setImageDrawable(mAnimationPart1);
            view.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
            mAnimationPart1.start();
        }
    };
}
