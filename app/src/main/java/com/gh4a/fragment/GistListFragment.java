package com.gh4a.fragment;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;

import com.gh4a.R;
import com.gh4a.activities.GistActivity;
import com.gh4a.adapter.GistAdapter;
import com.gh4a.adapter.RootAdapter;
import com.gh4a.loader.GistListLoader;
import com.gh4a.loader.LoaderResult;
import com.gh4a.loader.StarredGistListLoader;

import org.eclipse.egit.github.core.Gist;

import java.util.List;

public class GistListFragment extends ListDataBaseFragment<Gist> implements
        RootAdapter.OnItemClickListener<Gist> {
    public static GistListFragment newInstance(String userLogin, boolean starred) {
        Bundle args = new Bundle();
        args.putString("user", userLogin);
        args.putBoolean("starred", starred);

        GistListFragment f = new GistListFragment();
        f.setArguments(args);
        return f;
    }

    private String mUserLogin;
    private boolean mShowStarred;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLogin = getArguments().getString("user");
        mShowStarred = getArguments().getBoolean("starred");
    }

    @Override
    public Loader<LoaderResult<List<Gist>>> onCreateLoader() {
        if (mShowStarred) {
            return new StarredGistListLoader(getActivity());
        }
        return new GistListLoader(getActivity(), mUserLogin);
    }

    @Override
    protected RootAdapter<Gist, ? extends RecyclerView.ViewHolder> onCreateAdapter() {
        GistAdapter adapter = new GistAdapter(getActivity(), mUserLogin);
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected int getEmptyTextResId() {
        return mShowStarred ? R.string.no_starred_gists_found : R.string.no_gists_found;
    }

    @Override
    public void onItemClick(Gist gist) {
        startActivity(GistActivity.makeIntent(getActivity(), gist.getId()));
    }
}