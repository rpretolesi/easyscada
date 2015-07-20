package com.pretolesi.easyscada.CommClientData;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.pretolesi.SQL.SQLContract;
import com.pretolesi.easyscada.BaseFragment;
import com.pretolesi.easyscada.LoadersUtils.Loaders;
import com.pretolesi.easyscada.Settings.SettingsActivity;

/**
 *
 */
public class TranspProtocolClientListFragment extends ListFragment implements
//        SearchView.OnQueryTextListener,
//        SearchView.OnCloseListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private TranspProtocolClientFragmentCallbacks mCallbacks;
    private SimpleCursorAdapter mAdapter;
    // The SearchView for doing filtering.
//    SearchView mSearchView;

    // If non-null, this is the current filter the user has provided.
//    String mCurFilter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TranspProtocolClientListFragment newInstance(int sectionNumber, int position, long lClientTranspProtocol) {
        TranspProtocolClientListFragment fragment = new TranspProtocolClientListFragment();
        Bundle args = new Bundle();
        args.putInt(BaseFragment.ARG_SECTION_NUMBER, sectionNumber);
        args.putInt(BaseFragment.POSITION, position);
        args.putLong(BaseFragment.CLIENT_TRANSP_PROTOCOL, lClientTranspProtocol);
        fragment.setArguments(args);
        return fragment;
    }

    public TranspProtocolClientListFragment() {
    }

    @Override
    public void  onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        if(getArguments().getLong(BaseFragment.CLIENT_TRANSP_PROTOCOL) == TranspProtocolData.TranspProtocolType.TCP_IP.getID()){
            setEmptyText("No Tcp Ip Server");
        }
        if(getArguments().getLong(BaseFragment.CLIENT_TRANSP_PROTOCOL) == TranspProtocolData.TranspProtocolType.BLUETOOTH.getID()){
            setEmptyText("No Bluetooth Server");
        }

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);
        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_2,
                null,
                new String[] {SQLContract.TranspProtocolEntry.COLUMN_NAME_NAME},
                new int[] {android.R.id.text1}, 0);
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
       // getLoaderManager().initLoader(Loaders.TCP_IP_CLIENT_LOADER_ID, null, this);
    }
/*
    public static class ListSearchView extends SearchView {
        public ListSearchView(Context context) {
            super(context);
        }

        // The normal SearchView doesn't clear its search text when
        // collapsed, so we will do this for it.
        @Override
        public void onActionViewCollapsed() {
            setQuery("", false);
            super.onActionViewCollapsed();
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Place an action bar item for searching.
        MenuItem item = menu.add("Search");
        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchView = new ListSearchView(getActivity());
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setIconifiedByDefault(true);
        item.setActionView(mSearchView);
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (TranspProtocolClientFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement TranspProtocolClientFragmentCallbacks.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SettingsActivity) getActivity()).onSectionAttached(getArguments().getInt(BaseFragment.ARG_SECTION_NUMBER));
        ((SettingsActivity) getActivity()).restoreActionBar();
        getLoaderManager().initLoader(Loaders.TRANSP_PROTOCOL_LOADER_ID, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(Loaders.TRANSP_PROTOCOL_LOADER_ID);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallbacks.onTranspProtocolClientFragmentCallbacksListener(getArguments().getInt(BaseFragment.ARG_SECTION_NUMBER), getArguments().getInt(BaseFragment.POSITION), id, getArguments().getInt(BaseFragment.CLIENT_TRANSP_PROTOCOL));
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }
/*
    @Override
    public boolean onClose() {
        if (!TextUtils.isEmpty(mSearchView.getQuery())) {
            mSearchView.setQuery(null, true);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        String newFilter = !TextUtils.isEmpty(s) ? s : null;
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (mCurFilter == null && newFilter == null) {
            return true;
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true;
        }
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(Loaders.TCP_IP_CLIENT_LOADER_ID, null, this);

        return true;
    }
*/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if(id == Loaders.TRANSP_PROTOCOL_LOADER_ID){
            return new CursorLoader(getActivity()){
                @Override
                public Cursor loadInBackground() {
                    return SQLContract.TranspProtocolEntry.loadByTranspProtocol(getArguments().getLong(BaseFragment.CLIENT_TRANSP_PROTOCOL));
                }
            };
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        if(loader.getId() == Loaders.TRANSP_PROTOCOL_LOADER_ID) {
            mAdapter.swapCursor(cursor);

            // The list should now be shown.
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        if(loader.getId() == Loaders.TRANSP_PROTOCOL_LOADER_ID) {
            mAdapter.swapCursor(null);
        }

    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface TranspProtocolClientFragmentCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onTranspProtocolClientFragmentCallbacksListener(int sectionNumber, int position, long id, int iType);
    }
}
