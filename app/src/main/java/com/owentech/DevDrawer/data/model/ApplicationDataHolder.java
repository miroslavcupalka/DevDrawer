package com.owentech.DevDrawer.data.model;

import android.graphics.drawable.Drawable;

import com.owentech.DevDrawer.appwidget.DDWidgetViewsFactory;

import java.util.Comparator;

/**
 * @author Miroslav Cupalka
 * @package com.owentech.DevDrawer.data.model
 * @since 31.01.2018
 */
public class ApplicationDataHolder {
    public String name;
    public String packageName;
    public Drawable icon;
    public long firstInstalled;
    public long lastUpdate;
    public String versionName;
    public int versionCode;

    public static Comparator<ApplicationDataHolder> SORT_BY_LAST_UPDATE_ASC = new Comparator<ApplicationDataHolder>() {
        @Override
        public int compare(ApplicationDataHolder applicationDataHolder1, ApplicationDataHolder applicationDataHolder2) {

            int i = applicationDataHolder1.lastUpdate == applicationDataHolder2.lastUpdate ? 0 : applicationDataHolder1.lastUpdate < applicationDataHolder2.lastUpdate ? 1 : -1;

            if (i == 0) {
                i = Math.random() < 0.5 ? 1 : -1;
            }
            return i;
        }
    };

    public static Comparator<ApplicationDataHolder> SORT_BY_PACKAGE_NAME_ASC = new Comparator<ApplicationDataHolder>() {
        @Override
        public int compare(ApplicationDataHolder applicationDataHolder1, ApplicationDataHolder applicationDataHolder2) {
            return applicationDataHolder1.packageName.compareToIgnoreCase(applicationDataHolder2.packageName);
        }
    };
}
