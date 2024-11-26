package androidsamples.java.tictactoe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private LoginViewModel viewModel;
    private FirebaseManager firebaseManager;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = FirebaseManager.getInstance();
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText etEmail = view.findViewById(R.id.edit_email);
        EditText etPassword = view.findViewById(R.id.edit_password);
        EditText etConfirmPassword = view.findViewById(R.id.edit_confirm_password);

        etEmail.setText(viewModel.getEmail());
        etPassword.setText(viewModel.getPassword());

        view.findViewById(R.id.btn_register)
                .setOnClickListener(v -> {
                    Log.d(TAG, "Register Button Pressed");
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String confirmPassword = etConfirmPassword.getText().toString().trim();

                    if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        SnackbarHelper.showSnackbar(v, "All fields are required", Snackbar.LENGTH_SHORT, R.color.design_default_color_error);
                        return;
                    }

                    if (!password.equals(confirmPassword)) {
                        SnackbarHelper.showSnackbar(v, "Passwords do not match", Snackbar.LENGTH_SHORT, R.color.design_default_color_error);
                        return;
                    }
                    firebaseManager.signUp(email, password, new FirebaseManager.OnAuthCompleteListener() {
                        @Override
                        public void onSuccess() {
                            SnackbarHelper.showSnackbar(v, "Registration successful. Please log in.");
                            viewModel.clear();
                            NavDirections action = RegisterFragmentDirections.actionRegisterToLogin();
                            Navigation.findNavController(v).navigate(action);
                            // TODO: Build view model for registration
                            // clear the view model
                        }
                        
                        @Override
                        public void onError(Exception e) {
                            if(e instanceof FirebaseAuthWeakPasswordException){
                                Log.w(TAG, "signUp:onError: weak password - " + e.getMessage());
                                SnackbarHelper.showSnackbar(v, "Password is too weak", Snackbar.LENGTH_LONG, R.color.design_default_color_error);
                                // clear password
                                view.findViewById(R.id.edit_password).clearFocus();
                                view.findViewById(R.id.edit_confirm_password).clearFocus();
                                
                                // TODO: clear view model password
                                
                                // set focus to enter password
                                view.findViewById(R.id.edit_password).requestFocus();
                                
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Log.w(TAG, "signUp:onError: invalid email - " + e.getMessage());
                                SnackbarHelper.showSnackbar(v, "Invalid email", Snackbar.LENGTH_LONG, R.color.design_default_color_error);
                                // clear email and pass
                                view.findViewById(R.id.edit_email).clearFocus();
                                view.findViewById(R.id.edit_password).clearFocus();
                                view.findViewById(R.id.edit_confirm_password).clearFocus();
                                
                                // TODO: clear view model email and password
                                
                                // set focus to enter email
                                view.findViewById(R.id.edit_email).requestFocus();
                                
                            } else if (e instanceof FirebaseAuthUserCollisionException) {
                                Log.w(TAG, "signUp:onError: user already exists - " + e.getMessage());
                                SnackbarHelper.showSnackbar(v, "User already exists", Snackbar.LENGTH_LONG, R.color.design_default_color_error);
                                
                                // clear email and pass
                                view.findViewById(R.id.edit_email).clearFocus();
                                view.findViewById(R.id.edit_password).clearFocus();
                                view.findViewById(R.id.edit_confirm_password).clearFocus();
                                
                                // TODO: clear view model email and password
                                
                                // set focus to enter email
                                view.findViewById(R.id.edit_email).requestFocus();
                                
                            } else {
                                Log.e(TAG, "signUp:onError: failed to register", e);
                                SnackbarHelper.showSnackbar(v, "Failed to register", Snackbar.LENGTH_LONG, R.color.design_default_color_error);
                                // clear password
                            }
                        }
                    });


                });

        return view;
    }
}
