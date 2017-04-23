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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gh4a.R;
import com.gh4a.activities.CommitHistoryActivity;
import com.gh4a.adapter.FileAdapter;
import com.gh4a.adapter.RootAdapter;
import com.gh4a.loader.ContentListLoader;
import com.gh4a.loader.LoaderResult;
import com.gh4a.utils.StringUtils;
import com.gh4a.widget.ContextMenuAwareRecyclerView;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContentListFragment extends ListDataBaseFragment<RepositoryContents> implements
        RootAdapter.OnItemClickListener<RepositoryContents> {
    private static final int MENU_HISTORY = Menu.FIRST + 1;

    private Repository mRepository;
    private String mPath;
    private String mRef;

    private ParentCallback mCallback;
    private FileAdapter mAdapter;

    public interface ParentCallback {
        void onContentsLoaded(ContentListFragment fragment, List<RepositoryContents> contents);
        void onTreeSelected(RepositoryContents content);
        Set<String> getSubModuleNames(ContentListFragment fragment);
    }

    public static ContentListFragment newInstance(Repository repository,
            String path, ArrayList<RepositoryContents> contents, String ref) {
        ContentListFragment f = new ContentListFragment();

        Bundle args = new Bundle();
        args.putString("path", path);
        args.putString("ref", ref);
        args.putSerializable("repo", repository);
        args.putSerializable("contents", contents);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepository = (Repository) getArguments().getSerializable("repo");
        mPath = getArguments().getString("path");
        mRef = getArguments().getString("ref");
        if (StringUtils.isBlank(mRef)) {
            mRef = mRepository.getDefaultBranch();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof ParentCallback) {
            mCallback = (ParentCallback) getParentFragment();
        } else if (context instanceof ParentCallback) {
            mCallback = (ParentCallback) context;
        } else {
            throw new ClassCastException("No callback provided");
        }
    }

    @Override
    protected RootAdapter<RepositoryContents, ?> onCreateAdapter() {
        mAdapter = new FileAdapter(getActivity());
        mAdapter.setSubModuleNames(mCallback.getSubModuleNames(this));
        mAdapter.setContextMenuSupported(true);
        mAdapter.setOnItemClickListener(this);
        return mAdapter;
    }

    @Override
    protected void onRecyclerViewInflated(RecyclerView view, LayoutInflater inflater) {
        super.onRecyclerViewInflated(view, inflater);
        registerForContextMenu(view);
    }

    @Override
    protected int getEmptyTextResId() {
        return R.string.no_files_found;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        ContextMenuAwareRecyclerView.RecyclerContextMenuInfo info =
                (ContextMenuAwareRecyclerView.RecyclerContextMenuInfo) menuInfo;
        RepositoryContents contents = mAdapter.getItemFromAdapterPosition(info.position);
        Set<String> subModules = mCallback.getSubModuleNames(this);

        if (subModules == null || !subModules.contains(contents.getName())) {
            menu.add(Menu.NONE, MENU_HISTORY, Menu.NONE, R.string.history);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuAwareRecyclerView.RecyclerContextMenuInfo info =
                (ContextMenuAwareRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        if (info.position >= mAdapter.getItemCount()) {
            return false;
        }

        int id = item.getItemId();
        if (id == MENU_HISTORY) {
            RepositoryContents contents = mAdapter.getItemFromAdapterPosition(info.position);
            Intent intent = CommitHistoryActivity.makeIntent(getActivity(),
                    mRepository.getOwner().getLogin(), mRepository.getName(),
                    mRef, contents.getPath());
            startActivity(intent);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    public String getPath() {
        return mPath;
    }

    public void onSubModuleNamesChanged(Set<String> subModules) {
        if (mAdapter != null) {
            mAdapter.setSubModuleNames(subModules);
        }
    }

    @Override
    protected void onAddData(RootAdapter<RepositoryContents, ?> adapter, List<RepositoryContents> data) {
        super.onAddData(adapter, data);
        mCallback.onContentsLoaded(this, data);
    }

    @Override
    public void onItemClick(RepositoryContents content) {
        mCallback.onTreeSelected(content);
    }

    @Override
    public Loader<LoaderResult<List<RepositoryContents>>> onCreateLoader() {
        ContentListLoader loader = new ContentListLoader(getActivity(),
                mRepository.getOwner().getLogin(), mRepository.getName(), mPath, mRef);
        @SuppressWarnings("unchecked")
        ArrayList<RepositoryContents> contents =
                (ArrayList<RepositoryContents>) getArguments().getSerializable("contents");
        if (contents != null) {
            loader.prefillData(contents);
        }
        return loader;
    }
}