package io.github.UltimateBrowserProject.Unit;

import java.util.ArrayList;

public class TabStorage {

    public class Tab {
        public String
                url     = "",
                title   = "";

        public Tab() {}
        public Tab(String url) { setUrl(url);   }
        public Tab(String url, String title) {
            setUrl(url);
            setTitle(title);
        }

        public String getUrl()      { return this.url;      }
        public String getTitle()    { return this.title;    }

        public void setUrl  (String url     ) { this.url   = url;   }
        public void setTitle(String title   ) { this.title = title; }
    }

    public class TabNotFoundException extends Exception {
        public TabNotFoundException()               { super(); }
        public TabNotFoundException(String message) { super(message); }
        public TabNotFoundException(String urlOrTitle, boolean isUrl) {
            this("Tab with " +
                    (isUrl ? "URL" : "title") + " \""
                    + urlOrTitle + "\" not found.");
        }
    }

    public ArrayList<Tab> tabs = null;

    public TabStorage() {
        tabs = new ArrayList<Tab>();
    }

    public TabStorage(ArrayList<Tab> tabs) {
        this.tabs = tabs;
    }

    public TabStorage(String url, String title) {
        this();
        tabs.add(new Tab(url, title));
    }

    public TabStorage(String[] urls, String[] titles) {
        this();
        for ( int i = 0; i < urls.length; i++ ) {
            tabs.add(new Tab(urls[i], titles[i]));
        }
    }

    public void addTab(String url) {
        tabs.add(new Tab(url, ""));
    }

    public void removeTabByUrl(String url) {
        for ( int i = 0; i < tabs.size(); i++ ) {
            if(tabs.get(i).url.equals(url)) {
                tabs.remove(i);
                return;
            }
        }
    }

    public void removeTabByIndex(int index) {
        tabs.remove(index);
    }

    public void removeTabByTitle(String title) {
        for ( int i = 0; i < tabs.size(); i++ ) {
            if(tabs.get(i).title.equals(title)) {
                tabs.remove(i);
                return;
            }
        }
    }

    public Tab getTabByIndex(int index) {
        return tabs.get(index);
    }

    public Tab getTabByUrl(String url) throws TabNotFoundException {
        for ( int i = 0; i < tabs.size(); i++ ) {
            if(tabs.get(i).url.equals(url)) return tabs.get(i);
        }
        throw new TabNotFoundException(url, true);
    }

    public Tab getTabByTitle(String title) throws TabNotFoundException {
        for ( int i = 0; i < tabs.size(); i++ ) {
            if(tabs.get(i).title.equals(title)) return tabs.get(i);
        }
        throw new TabNotFoundException(title, false);
    }

    public void clearAll() {
        tabs.clear();
        this.tabs = new ArrayList<Tab>();
    }

    public ArrayList<Tab> getAllTabs() {
        return tabs;
    }

    public Tab[] getTabArray() {
        Tab[] allTabs = new Tab[tabs.size()];

        for ( int i = 0; i < allTabs.length; i++ )
            allTabs[i] = tabs.get(i);

        return allTabs;
    }

}
