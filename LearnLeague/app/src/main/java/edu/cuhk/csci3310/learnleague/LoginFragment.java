package edu.cuhk.csci3310.learnleague;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private String mParam1;
    private String mParam2;

    private View layoutToast;
    TextView toastText;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextView clickableText = view.findViewById(R.id.clickable_text);

        clickableText.setOnClickListener(v -> {
            ((LoginActivity) getActivity()).replaceFragment(new SignupFragment());
        });

        // register login button
        Button loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> submitLogin());

        // text view for toast
        layoutToast = inflater.inflate(R.layout.toast_style, null);
        toastText = layoutToast.findViewById(R.id.toast_text);

        return view;
    }

    /**
     * Process the login info.
     */
    private void submitLogin() {
        EditText accountid = getView().findViewById(R.id.id_input);
        EditText password = getView().findViewById(R.id.password_input);

        // Success to login
        if (validateLogin(accountid.getText().toString(), password.getText().toString())) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else {
            // Fail to login

            toastText.setText("Oops! It looks like the ID or password is invalid. Please check and re-enter.");

            Toast toast = new Toast(getActivity());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layoutToast);
            toast.show();
        }
    }

    private Boolean validateLogin(String accountid, String password) {
        return true;
    }
}