package com.pretolesi.easyscada.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.pretolesi.easyscada.R;

/**
 * Created by RPRETOLESI on 20/03/2015.
 */
public class SetNameAndOrientDialogFragment extends DialogFragment {

    private SetNameAndOrientDialogFragmentCallbacks mCallbacks;
    private AlertDialog m_AD;
    private EditText m_et;
    private RadioButton m_rl;
    private RadioButton m_rp;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String POSITION = "position";
    private static final String TITLE = "title";
    private static final String NAME = "name";
    private static final String DEFAULT_ORIENTATION = "default_orientation";

    public static SetNameAndOrientDialogFragment newInstance(int position, String strTitle, String strName,  boolean bLandscape) {
        SetNameAndOrientDialogFragment fragment = new SetNameAndOrientDialogFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putString(TITLE, strTitle);
        args.putString(NAME, strName);
        args.putBoolean(DEFAULT_ORIENTATION, bLandscape);
        fragment.setArguments(args);
        return fragment;
    }

    public SetNameAndOrientDialogFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (SetNameAndOrientDialogFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement SetNameDialogFragmentCallbacks.");
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
     }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = getActivity().getLayoutInflater().inflate(R.layout.set_name_dialog_fragment, null);
        m_et = (EditText)view.findViewById(R.id.id_et_name);
        m_rl = (RadioButton)view.findViewById(R.id.radioButtonLandscape);
        m_rp = (RadioButton)view.findViewById(R.id.radioButtonPortrait);
        if(m_et != null){
            if(getArguments().getString(NAME) != null && !getArguments().getString(NAME).equals("")){
                m_et.setText(getArguments().getString(NAME));
            } else {
                m_et.setText("My Room");
            }
        }
        if(getArguments().getBoolean(DEFAULT_ORIENTATION)){
            if (m_rl != null) {
                m_rl.setChecked(true);
            }
        } else {
            if (m_rp != null) {
                m_rp.setChecked(true);
            }
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle(getArguments().getString(TITLE));
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mCallbacks != null) {
                            String str = "";
                            boolean bLandscape = false;
                             if (m_et != null) {
                                str = m_et.getText().toString();
                            }
                            if (m_rl != null) {
                                bLandscape = m_rl.isChecked();;
                            }
                            mCallbacks.onSetNameAndOrientDialogFragmentClickListener(getArguments().getInt(POSITION), getArguments().getString(TITLE), str, bLandscape);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SetNameAndOrientDialogFragment.this.getDialog().cancel();
                    }
                });
        m_AD = builder.create();
        return m_AD;
    }
    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface SetNameAndOrientDialogFragmentCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onSetNameAndOrientDialogFragmentClickListener(int position, String strTitle, String strName, boolean bLandscape);
    }
}
