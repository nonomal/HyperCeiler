/*
  * This file is part of HyperCeiler.

  * HyperCeiler is free software: you can redistribute it and/or modify
  * it under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation, either version 3 of the
  * License.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.

  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <https://www.gnu.org/licenses/>.

  * Copyright (C) 2023-2024 HyperCeiler Contributions
*/
package com.sevtinge.hyperceiler.ui.sub.home;

import static com.sevtinge.hyperceiler.utils.devicesdk.MiDeviceAppUtilsKt.isPad;
import static com.sevtinge.hyperceiler.utils.devicesdk.SystemSDKKt.isMoreHyperOSVersion;

import android.view.View;

import com.sevtinge.hyperceiler.R;
import com.sevtinge.hyperceiler.ui.dashboard.DashboardFragment;
import com.sevtinge.hyperceiler.ui.fragment.base.SettingsPreferenceFragment;

import fan.preference.SwitchPreference;

public class HomeRecentSettings extends DashboardFragment {

    SwitchPreference mDimming;
    SwitchPreference mShowMenInfo;
    SwitchPreference mHideCleanIcon;
    SwitchPreference mNotHideCleanIcon;

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.home_recent;
    }

    @Override
    public void initPrefs() {
        mShowMenInfo = findPreference("prefs_key_home_recent_show_memory_info");
        mHideCleanIcon = findPreference("prefs_key_home_recent_hide_clean_up");
        mNotHideCleanIcon = findPreference("prefs_key_always_show_clean_up");
        mDimming = findPreference("prefs_key_home_recent_disable_wallpaper_dimming");

        mDimming.setVisible(!isMoreHyperOSVersion(1f));
        mShowMenInfo.setVisible(isPad());

        mHideCleanIcon.setOnPreferenceChangeListener((preference, o) -> {
            if (!(boolean) o) {
                mNotHideCleanIcon.setChecked(false);
            }
            return true;
        });
    }
}