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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.gh4a.Constants;
import com.gh4a.FollowerFollowingListActivity;
import com.gh4a.Gh4Application;
import com.gh4a.GistListActivity;
import com.gh4a.OrganizationListActivity;
import com.gh4a.OrganizationMemberListActivity;
import com.gh4a.R;
import com.gh4a.RepositoryListActivity;
import com.gh4a.loader.RepositoryListLoader;
import com.gh4a.loader.UserLoader;
import com.gh4a.utils.ImageDownloader;
import com.gh4a.utils.StringUtils;

public class UserFragment extends SherlockFragment implements 
    OnClickListener, LoaderManager.LoaderCallbacks<Object> {

    private String mUserLogin;
    private String mUserName;
    private User mUser;

    public static UserFragment newInstance(String login, String name) {
        UserFragment f = new UserFragment();

        Bundle args = new Bundle();
        args.putString(Constants.User.USER_LOGIN, login);
        args.putString(Constants.User.USER_NAME, name);
        f.setArguments(args);
        
        return f;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, ">>>>>>>>>>> onCreate UserFragment");
        super.onCreate(savedInstanceState);
        mUserLogin = getArguments().getString(Constants.User.USER_LOGIN);
        mUserName = getArguments().getString(Constants.User.USER_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, ">>>>>>>>>>> onCreateView UserFragment");
        View v = inflater.inflate(R.layout.user, container, false);
        setRetainInstance(true);
        return v;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(Constants.LOG_TAG, ">>>>>>>>>>> onActivityCreated UserFragment");
        super.onActivityCreated(savedInstanceState);
        
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().getLoader(0).forceLoad();
    }
    
    private void fillData() {
        View v = getView();
        Gh4Application app = (Gh4Application) getSherlockActivity().getApplication();
        Typeface boldCondensed = app.boldCondensed;
        Typeface condensed = app.condensed;
        Typeface regular = app.regular;
        Typeface italic = app.italic;
        
        ImageView ivGravatar = (ImageView) v.findViewById(R.id.iv_gravatar);
        ImageDownloader.getInstance().download(mUser.getGravatarId(), ivGravatar, 80);

        TextView tvName = (TextView) v.findViewById(R.id.tv_name);
        tvName.setTypeface(boldCondensed);
        
        TextView tvCreated = (TextView) v.findViewById(R.id.tv_created_at);
        tvCreated.setTypeface(app.regular);
        
        TextView tvFollowersCount = (TextView) v.findViewById(R.id.tv_followers_count);
        tvFollowersCount.setTypeface(boldCondensed);
        tvFollowersCount.setText(String.valueOf(mUser.getFollowers()));
        
        TableLayout tlFollowers = (TableLayout) v.findViewById(R.id.cell_followers);
        tlFollowers.setOnClickListener(this);
        
        TextView tvFollowers = (TextView) v.findViewById(R.id.tv_followers_label);
        tvFollowers.setTypeface(condensed);
        if (Constants.User.USER_TYPE_USER.equals(mUser.getType())) {
            tvFollowers.setText(R.string.user_followers);
        }
        else {
            tvFollowers.setText(R.string.user_members);
        }
        
        //hide following if organization
        TextView tvFollowing = (TextView) v.findViewById(R.id.tv_following_label);
        if (Constants.User.USER_TYPE_USER.equals(mUser.getType())) {
            tvFollowing.setTypeface(condensed);
            tvFollowing.setVisibility(View.VISIBLE);
            
            TextView tvFollowingCount = (TextView) v.findViewById(R.id.tv_following_count);
            tvFollowingCount.setTypeface(boldCondensed);
            tvFollowingCount.setText(String.valueOf(mUser.getFollowing()));
            
            TableLayout tl = (TableLayout) v.findViewById(R.id.cell_following);
            tl.setOnClickListener(this);
        }
        else {
            tvFollowing.setVisibility(View.GONE);
        }
        
        //hide organizations if organization
        TextView tvOrgCount = (TextView) v.findViewById(R.id.tv_organizations_count);
        TextView tvOrg = (TextView) v.findViewById(R.id.tv_organizations_label);
        if (Constants.User.USER_TYPE_USER.equals(mUser.getType())) {
            tvOrg.setTypeface(condensed);
            tvOrg.setVisibility(View.VISIBLE);

            tvOrgCount.setTypeface(boldCondensed);
            tvOrgCount.setVisibility(View.VISIBLE);
            
            TableLayout tl = (TableLayout) v.findViewById(R.id.cell_organizations);
            tl.setOnClickListener(this);
        }
        else {
            tvOrg.setVisibility(View.GONE);
            tvOrgCount.setVisibility(View.GONE);
        }
        
        TextView tvGistCount = (TextView) v.findViewById(R.id.tv_gists_count);
        TextView tvGist = (TextView) v.findViewById(R.id.tv_gists_label);
        if (Constants.User.USER_TYPE_USER.equals(mUser.getType())) {
            tvGist.setTypeface(condensed);
            tvGist.setVisibility(View.VISIBLE);
            
            tvGistCount.setTypeface(boldCondensed);
            tvGistCount.setVisibility(View.VISIBLE);
            
            TableLayout tl = (TableLayout) v.findViewById(R.id.cell_gists);
            tl.setOnClickListener(this);
        }
        else {
            tvGist.setVisibility(View.GONE);
            tvGistCount.setVisibility(View.GONE);
        }

        tvName.setText(StringUtils.formatName(mUser.getLogin(), mUser.getName()));
        if (Constants.User.USER_TYPE_ORG.equals(mUser.getType())) {
            tvName.append(" (");
            tvName.append(Constants.User.USER_TYPE_ORG);
            tvName.append(")");
        }
        
        tvCreated.setText(mUser.getCreatedAt() != null ? 
                getResources().getString(R.string.user_created_at,
                        StringUtils.formatDate(mUser.getCreatedAt())) : "");

        //show email row if not blank
        TextView tvEmail = (TextView) v.findViewById(R.id.tv_email);
        tvEmail.setTypeface(regular);
        if (!StringUtils.isBlank(mUser.getEmail())) {
            tvEmail.setText(mUser.getEmail());
            tvEmail.setVisibility(View.VISIBLE);
        }
        else {
            tvEmail.setVisibility(View.GONE);
        }
        
        //show website if not blank
        TextView tvWebsite = (TextView) v.findViewById(R.id.tv_website);
        if (!StringUtils.isBlank(mUser.getBlog())) {
            tvWebsite.setText(mUser.getBlog());
            tvWebsite.setVisibility(View.VISIBLE);
        }
        else {
            tvWebsite.setVisibility(View.GONE);
        }
        tvWebsite.setTypeface(regular);
        
        //show company if not blank
        TextView tvCompany = (TextView) v.findViewById(R.id.tv_company);
        if (!StringUtils.isBlank(mUser.getCompany())) {
            tvCompany.setText(mUser.getCompany());
            tvCompany.setVisibility(View.VISIBLE);
        }
        else {
            tvCompany.setVisibility(View.GONE);
        }
        tvCompany.setTypeface(regular);
        
        //Show location if not blank
        TextView tvLocation = (TextView) v.findViewById(R.id.tv_location);
        if (!StringUtils.isBlank(mUser.getLocation())) {
            tvLocation.setText(mUser.getLocation());
            tvLocation.setVisibility(View.VISIBLE);
        }
        else {
            tvLocation.setVisibility(View.GONE);
        }
        tvLocation.setTypeface(regular);
        
        TextView tvPubRepo = (TextView) v.findViewById(R.id.tv_pub_repos_label);
        tvPubRepo.setTypeface(boldCondensed);
        tvPubRepo.setTextColor(Color.parseColor("#0099cc"));
        
        getLoaderManager().initLoader(1, null, this);
        getLoaderManager().getLoader(1).forceLoad();
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
        case R.id.cell_followers:
            getFollowers(view);
            break;
        case R.id.cell_following:
            getFollowing(view);
            break;
        case R.id.cell_organizations:
            getOrganizations(view);
            break;
        case R.id.cell_gists:
            getGists(view);
            break;
        default:
            break;
        }
    }

    public void getPublicRepos(View view) {
        Intent intent = new Intent().setClass(this.getActivity(), RepositoryListActivity.class);
        intent.putExtra(Constants.User.USER_LOGIN, mUserLogin);
        intent.putExtra(Constants.User.USER_NAME, mUserName);
        intent.putExtra(Constants.User.USER_TYPE, mUser.getType());
        startActivity(intent);
    }

    public void getFollowers(View view) {
        if (Constants.User.USER_TYPE_ORG.equals(mUser.getType())) {
            Intent intent = new Intent().setClass(this.getActivity(), OrganizationMemberListActivity.class);
            intent.putExtra(Constants.Repository.REPO_OWNER, mUserLogin);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent().setClass(this.getActivity(), FollowerFollowingListActivity.class);
            intent.putExtra(Constants.User.USER_LOGIN, mUserLogin);
            if (Constants.User.USER_TYPE_USER.equals(mUser.getType())) {
                intent.putExtra(Constants.SUBTITLE, getResources().getString(R.string.user_followers));
            }
            else {
                intent.putExtra(Constants.SUBTITLE, getResources().getString(R.string.user_members));
            }
            intent.putExtra(Constants.FIND_FOLLOWER, true);
            startActivity(intent);
        }
    }

    public void getFollowing(View view) {
        Intent intent = new Intent().setClass(this.getActivity(), FollowerFollowingListActivity.class);
        intent.putExtra(Constants.User.USER_LOGIN, mUserLogin);
        intent.putExtra(Constants.ACTIONBAR_TITLE, mUserLogin
                + (!StringUtils.isBlank(mUserName) ? " - " + mUserName : ""));
        intent.putExtra(Constants.SUBTITLE, getResources().getString(R.string.user_following));
        intent.putExtra(Constants.FIND_FOLLOWER, false);
        startActivity(intent);
    }

    public void getOrganizations(View view) {
        Intent intent = new Intent().setClass(this.getActivity(), OrganizationListActivity.class);
        intent.putExtra(Constants.User.USER_LOGIN, mUserLogin);
        startActivity(intent);
    }
    
    public void getGists(View view) {
        Intent intent = new Intent().setClass(this.getActivity(), GistListActivity.class);
        intent.putExtra(Constants.User.USER_LOGIN, mUserLogin);
        startActivity(intent);
    }

    public void fillTopRepos(List<Repository> repos) {
        Gh4Application app = (Gh4Application) getSherlockActivity().getApplication();
        Typeface boldCondensed = app.boldCondensed;
        Typeface regular = app.regular;
        Typeface italic = app.italic;
        
        View v = getView();
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.ll_top_repos);
        
        for (final Repository repository : repos) {
            View rowView = getLayoutInflater(null).inflate(R.layout.row_simple_3, null);
            rowView.setBackgroundResource(android.R.drawable.list_selector_background);
            rowView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gh4Application app = (Gh4Application) getSherlockActivity().getApplication();
                    app.openRepositoryInfoActivity(getSherlockActivity(), repository);
                }
            });
            
            TextView tvTitle = (TextView) rowView.findViewById(R.id.tv_title);
            tvTitle.setTypeface(boldCondensed);
            TextView tvDesc = (TextView) rowView.findViewById(R.id.tv_desc);
            tvDesc.setTypeface(regular);
            tvDesc.setSingleLine(true);
            TextView tvExtra = (TextView) rowView.findViewById(R.id.tv_extra);
            tvExtra.setTypeface(italic);
            
            tvTitle.setText(repository.getOwner().getLogin() + " / " + repository.getName());
            
            if (!StringUtils.isBlank(repository.getDescription())) {
                tvDesc.setVisibility(View.VISIBLE);
                tvDesc.setText(StringUtils.doTeaser(repository.getDescription()));
            }
            else {
                tvDesc.setVisibility(View.GONE);
            }
            
            String extraData = (repository.getLanguage() != null ? repository.getLanguage()
                    + " | " : "")
                    + StringUtils.toHumanReadbleFormat(repository.getSize())
                    + " | "
                    + repository.getForks()
                    + " forks | "
                    + repository.getWatchers()
                    + " watchers";
            tvExtra.setText(extraData);
            
            View divider = new View(getActivity());
            divider.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 16));
            
            ll.addView(rowView);
            ll.addView(divider);
        }
        
        TextView tvMore = new TextView(getSherlockActivity());
        tvMore.setTypeface(italic);
        tvMore.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        tvMore.setBackgroundResource(android.R.drawable.list_selector_background);
        if (!repos.isEmpty()) {
            tvMore.setText("View more");
            tvMore.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    getPublicRepos(view);
                }
            });
        }
        else {
            tvMore.setText("Repositories not found");
        }
        ll.addView(tvMore);
    }
    
    @Override
    public Loader onCreateLoader(int id, Bundle arg1) {
        if (id == 0) {
            return new UserLoader(getSherlockActivity(), mUserLogin);
        }
        else {
            Map<String, String> filterData = new HashMap<String, String>();
            filterData.put("sort", "pushed");
            return new RepositoryListLoader(getSherlockActivity(), mUserLogin, 
                    mUser.getType(), filterData, 5);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object object) {
        if (object != null) {
            if (loader instanceof UserLoader) {
                this.mUser = (User) object;
                fillData();
            }
            else if (loader instanceof RepositoryListLoader) {
                fillTopRepos((List<Repository>) object);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> arg0) {
        // TODO Auto-generated method stub
        
    }
    
}