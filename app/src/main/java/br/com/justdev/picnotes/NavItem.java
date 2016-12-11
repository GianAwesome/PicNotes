package br.com.justdev.picnotes;

/**
 * Created by Gian on 11/12/2016.
 */

// Baseado em http://codetheory.in/android-navigation-drawer/
public  class NavItem {
    private String mTitle;
    private String mSubtitle;
    private int mIcon;

    public NavItem(String title, String subtitle, int icon) {
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;
    }

    public String getTitle(){
        return this.mTitle;
    }

    public String getSubtitle(){
        return this.mSubtitle;
    }

    public int getIcon(){ return this.mIcon; }
}