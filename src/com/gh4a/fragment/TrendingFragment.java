/*
 * Copyright 2011 Azwan Adli Abdullah
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gh4a.fragment;

import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.content.Loader;

import com.gh4a.Gh4Application;
import com.gh4a.R;
import com.gh4a.adapter.RootAdapter;
import com.gh4a.adapter.TrendAdapter;
import com.gh4a.holder.Trend;
import com.gh4a.loader.LoaderResult;
import com.gh4a.loader.TrendLoader;

public class TrendingFragment extends ListDataBaseFragment<Trend> {
    private static final String URL_TEMPLATE = "http://github-trends.ryotarai.info/rss/github_trends_all_%s.rss";
    public static final String TYPE_DAILY = "daily";
    public static final String TYPE_WEEKLY = "weekly";
    public static final String TYPE_MONTHLY = "monthly";

    public String mUrl;

    public static TrendingFragment newInstance(String type) {
        TrendingFragment f = new TrendingFragment();

        Bundle args = new Bundle();
        args.putString("url", String.format(Locale.US, URL_TEMPLATE, type));
        f.setArguments(args);
        
        return f;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString("url");
    }
    
    @Override
    protected RootAdapter<Trend> onCreateAdapter() {
        return new TrendAdapter(getActivity());
    }

    @Override
    protected int getEmptyTextResId() {
        return R.string.no_trends_found;
    }

    @Override
    protected void onItemClick(Trend trend) {
        Gh4Application app = Gh4Application.get(getActivity());
        String[] repo = trend.getRepo();
        if (repo != null) {
            app.openRepositoryInfoActivity(getActivity(), repo[0], repo[1], null, 0);
        }
    }

    @Override
    public Loader<LoaderResult<List<Trend>>> onCreateLoader(int id, Bundle args) {
        return new TrendLoader(getActivity(), mUrl);
    }
}