package com.owentech.DevDrawer.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.owentech.DevDrawer.R;
import com.owentech.DevDrawer.adapters.PartialMatchAdapter;
import com.owentech.DevDrawer.utils.OttoManager;
import com.owentech.DevDrawer.events.WidgetRenamedEvent;
import com.owentech.DevDrawer.utils.AppConstants;
import com.owentech.DevDrawer.utils.Database;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created by tonyowen on 15/07/2014.
 */
public class ChangeWidgetNameDialogFragment extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.editText) EditText editText;
    @BindView(R.id.ok) Button ok;

    private PartialMatchAdapter partialMatchAdapter;
    final private static String EDIT = "edit";
    final private static String WIDGET_ID = "widget_id";
    private int widgetId;
    private String edit;

    public ChangeWidgetNameDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static ChangeWidgetNameDialogFragment newInstance(int widget_id, String edit) {
        ChangeWidgetNameDialogFragment changeWidgetNameDialogFragment = new ChangeWidgetNameDialogFragment();
        Bundle args = new Bundle();
        args.putInt(WIDGET_ID, widget_id);
        args.putString(EDIT, edit);
        changeWidgetNameDialogFragment.setArguments(args);
        return changeWidgetNameDialogFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.rename_widget_dialog_fragment, container);
       ButterKnife.bind(this, view);
        ok.setOnClickListener(this);

        widgetId = getArguments().getInt(WIDGET_ID);
        edit = getArguments().getString(EDIT);

        if (edit != null && !edit.equalsIgnoreCase(AppConstants.UNNAMED)){
            editText.setText(edit);
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        OttoManager.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        OttoManager.getInstance().unregister(this);
    }

    @Override
    public void onClick(View view) {
        if (view == ok){
            Database.getInstance(getActivity()).renameWidget(widgetId, editText.getText().toString());
            OttoManager.getInstance().post(new WidgetRenamedEvent());
            getDialog().dismiss();
        }
    }
}
