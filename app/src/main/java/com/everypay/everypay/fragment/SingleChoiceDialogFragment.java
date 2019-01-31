
package com.everypay.everypay.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.everypay.everypay.R;
import com.everypay.everypay.adapter.GenericSpinnerAdapter;

import java.util.ArrayList;



public class SingleChoiceDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "ee.cyber.smartid.app.ARG_TITLE";
    private static final String ARG_MESSAGE = "ee.cyber.smartid.app.ARG_MESSAGE";
    private static final String ARG_CHOICES = "ee.cyber.smartid.app.ARG_CHOICES";
    private static final String ARG_EXTRAS = "ee.cyber.smartid.app.ARG_EXTRAS";

    public static SingleChoiceDialogFragment newInstance(String title, String message, ArrayList<String> choices) {
        return newInstance(title, message, choices, null);
    }

    public static SingleChoiceDialogFragment newInstance(String title, String message, ArrayList<String> choices, final Bundle extras) {
        SingleChoiceDialogFragment fragment = new SingleChoiceDialogFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putSerializable(ARG_CHOICES, choices);
        args.putBundle(ARG_EXTRAS, extras);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ListView listView = new ListView(getContext());
        final ArrayList<String> values = (ArrayList<String>) getArguments().getSerializable(ARG_CHOICES);
        final GenericSpinnerAdapter<String> adapter = new GenericSpinnerAdapter<>(getActivity(), R.layout.generic_list_item, values);
        listView.setAdapter(adapter);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ep_AppCompatAlertDialogStyle);
        builder.setTitle(getArguments().getString(ARG_TITLE));
        builder.setMessage(getArguments().getString(ARG_MESSAGE));
        builder.setView(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getListener() != null) {
                    getListener().onSingleChoicePicked(getTag(), position, getExtras());
                }
                dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (getListener() != null) {
            getListener().onSingleChoiceCanceled(getTag(), getExtras());
        }
    }

    private Bundle getExtras() {
        return getArguments().getBundle(ARG_EXTRAS);
    }

    private SingleChoiceDialogFragmentListener getListener() {
        if (getTargetFragment() instanceof SingleChoiceDialogFragmentListener) {
            return (SingleChoiceDialogFragmentListener) getTargetFragment();
        } else if (getActivity() instanceof SingleChoiceDialogFragmentListener) {
            return (SingleChoiceDialogFragmentListener) getActivity();
        }
        return null;
    }

    public interface SingleChoiceDialogFragmentListener {
        void onSingleChoicePicked(String tag, int position, Bundle extras);

        void onSingleChoiceCanceled(String tag, Bundle extras);
    }
}
