package com.ndg.intel.fashionconcierge;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LoginFragment extends Fragment implements TextView.OnEditorActionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "LoginFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;

    OnFragmentInteractionListener mListener;
    private LinearLayout loginFragment;
    private EditText loginEditText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        setHasOptionsMenu(true);
    }

    private void showLoginCard() {
        loginFragment.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_and_slide_up);
        loginFragment.startAnimation(animation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginFragment = (LinearLayout) view.findViewById(R.id.edit_card);

        // Setup the URL Edit Text handler
        loginEditText = (EditText) view.findViewById(R.id.login_edit_text);
        loginEditText.setOnEditorActionListener(this);

        Button button = (Button) view.findViewById(R.id.edit_card_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBleshWithPhoneNumber();
            }
        });

        showLoginCard();

        return view;
    }

    private void startBleshWithPhoneNumber() {

        // Write the url to the device
        try {
            String number = loginEditText.getText().toString();
            if(loginEditText!=null) {

                if(!checkSavedPhoneNumber(number)) {
                    mListener.onFragmentInteraction(number);
                }
                else {
                    Log.i(TAG, "Phone number has not changed!");
                }
            }
            else
            {
                Log.w(TAG, "number is empty!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkSavedPhoneNumber(String number) {

        SharedPreferences sp = getActivity().getSharedPreferences(BleshDemoMainActivity.PACKAGE_NAME, Context.MODE_PRIVATE);

        final String previousId = sp.getString(BleshDemoMainActivity.INTEGRATION_ID, BleshDemoMainActivity.INTEGRATION_ID);

        Log.i(TAG, "IntegrationID in sp " + previousId + " number:" + number);

        return number.equals(previousId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * This is the class that listens for specific text entry events
     * (e.g. the DONE key)
     * on the edit text field that the user uses
     * to enter a new url for the configurable beacon
     */
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        // If the keyboard "DONE" button was pressed
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            // Hide the software keyboard
            hideSoftKeyboard();
            startBleshWithPhoneNumber();
            return true;
        }
        return false;
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(loginFragment.getWindowToken(), 0);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String number);
    }

}
