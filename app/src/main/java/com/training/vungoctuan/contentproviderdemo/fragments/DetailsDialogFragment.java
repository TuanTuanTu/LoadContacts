package com.training.vungoctuan.contentproviderdemo.fragments;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.training.vungoctuan.contentproviderdemo.R;

/**
 * Created by vungoctuan on 2/22/18.
 */
public class DetailsDialogFragment extends DialogFragment
    implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final String TAG = "DetailsDialogFragment";
    private ImageView ivImageDetail;
    private TextView tvNameDetail;
    private TextView tvPhoneDetail;
    private ImageButton btnCall, btnMessage;
    private String mName, mPhone;

    public static DetailsDialogFragment newInstance() {
        return new DetailsDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivImageDetail = view.findViewById(R.id.ivImageDetail);
        tvNameDetail = view.findViewById(R.id.tvNameDetail);
        tvPhoneDetail = view.findViewById(R.id.tvPhoneDetail);
        mName = getArguments().getString("name");
        mPhone = getArguments().getString("phone");
        tvNameDetail.setText(mName);
        tvPhoneDetail.setText(String.format("Phone: %s", mPhone));
        btnCall = view.findViewById(R.id.btnCall);
        btnMessage = view.findViewById(R.id.btnMessage);
        btnCall.setOnClickListener(this);
        btnMessage.setOnClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onResume() {
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);
        super.onResume();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCall:
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mPhone));
                startActivity(callIntent);
                break;
            case R.id.btnMessage:
                Intent messageIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mPhone, null));
                startActivity(messageIntent);
                break;
            default:
                break;
        }
    }
}
