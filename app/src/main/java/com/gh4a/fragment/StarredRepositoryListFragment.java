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

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.StarService;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.gh4a.Gh4Application;
import com.gh4a.R;
import com.gh4a.activities.RepositoryActivity;
import com.gh4a.adapter.RepositoryAdapter;
import com.gh4a.adapter.RootAdapter;

import java.util.HashMap;

public class StarredRepositoryListFragment extends PagedDataBaseFragment<Repository> {
    public static StarredRepositoryListFragment newInstance(String login,
            String sortOrder, String sortDirection) {
        StarredRepositoryListFragment f = new StarredRepositoryListFragment();

        Bundle args = new Bundle();
        args.putString("user", login);
        args.putString("sort_order", sortOrder);
        args.putString("sort_direction", sortDirection);
        f.setArguments(args);

        return f;
    }

    private String mLogin;
    private String mSortOrder;
    private String mSortDirection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogin = getArguments().getString("user");
        mSortOrder = getArguments().getString("sort_order");
        mSortDirection = getArguments().getString("sort_direction");
    }

    @Override
    protected RootAdapter<Repository, ? extends RecyclerView.ViewHolder> onCreateAdapter() {
        return new RepositoryAdapter(getActivity());
    }

    @Override
    protected int getEmptyTextResId() {
        return R.string.no_starred_repos_found;
    }

    @Override
    public void onItemClick(Repository repository) {
        startActivity(RepositoryActivity.makeIntent(getActivity(), repository));
    }

    @Override
    protected PageIterator<Repository> onCreateIterator() {
        StarService starService = (StarService)
                Gh4Application.get().getService(Gh4Application.STAR_SERVICE);
        HashMap<String, String> filterData = new HashMap<>();
        filterData.put("sort", mSortOrder);
        filterData.put("direction", mSortDirection);
        return starService.pageStarred(mLogin, filterData);
    }
}