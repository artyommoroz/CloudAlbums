package com.frost.cloudalbums;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.frost.cloudalbums.api.SoundCloudService;
import com.frost.cloudalbums.model.AccessToken;
import com.frost.cloudalbums.util.PreferenceHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment {

    @Bind(R.id.email)
    EditText emailView;
    @Bind(R.id.password)
    EditText passwordView;
    @Bind(R.id.btnLogin)
    Button btnLogin;
    @Bind(R.id.login_progress)
    View progressView;
    @Bind(R.id.login_form)
    View loginFormView;

    private SharedPreferences preferences;
    private OnFragmentInteractionListener interactionListener;

    public LoginFragment() {}

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                showProgress(false);
            }
        });
    }

    private void attemptLogin() {
        emailView.setError(null);
        passwordView.setError(null);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Проверка валидности пароля
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Полверка валидности почтового ящика
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            performQuery(email, password);
        }
    }

    private void performQuery(String email, String password) {
        showProgress(true);
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SoundCloudService.BASE_URL_OLD)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        SoundCloudService service = retrofit.create(SoundCloudService.class);

        Call<AccessToken> call = service.getToken(email, password);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                showProgress(false);
                AccessToken accessToken = response.body();
                if (accessToken != null && interactionListener != null) {
                    PreferenceHelper.setUserLoggedIn(getActivity(), true);
                    PreferenceHelper.saveAccessToken(getActivity(), accessToken.getAccessToken());
                    interactionListener.onLoginButtonPressed();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_incorrect_email_or_password),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.no_connection_login_message),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isEmailValid(String email) {
        Pattern validEmailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = validEmailRegex.matcher(email);
        return matcher.find();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onLoginButtonPressed();
    }
}
