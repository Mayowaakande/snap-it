package com.app.snapsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.app.snapsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Sign up screen..
public class AuthenticationActivity extends BaseActivity {
    EditText edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegister, btnLogRegister;
    private FirebaseAuth mAuth;
    TextView textView2;
    public static String email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //All the Views of screen and firebase initialization..
        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.editPassword);
        edtConfirmPassword = findViewById(R.id.editConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogRegister = findViewById(R.id.btnLogRegister);


        //Press signUp button code..
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString().trim();
                password = edtPassword.getText().toString().trim();
                if (email.isEmpty()) {
                    edtEmail.setError("Required!");
                    edtEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    edtPassword.setError("Required!");
                    edtPassword.requestFocus();
                    return;
                }
                if (edtConfirmPassword.getText().toString().isEmpty()) {
                    edtConfirmPassword.setError("Required!");
                    edtConfirmPassword.requestFocus();
                    return;
                }
                if(!password.equals(edtConfirmPassword.getText().toString())){
                    edtConfirmPassword.setError("Does not match!");
                    edtConfirmPassword.requestFocus();
                    return;
                }
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()==false){
                    edtEmail.setError("Invalid email format!");
                    edtEmail.requestFocus();
                    return;
                }
                createAccount(email, password);
            }
        });

        btnLogRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // This function create user account in firebase authentication with email and password..
    private void createAccount(final String email, final String password) {
        if (!validateForm()) {
            return;
        }

        showProgressDialog("Please wait..");

        //This function create user account in firebase authentication with email and password
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendVerification();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

    //After storing email and password to firebase send verification email to user and move to verification screen..
    private void sendVerification() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    signIn();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signIn() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());
                if (task.isSuccessful()) {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Toast.makeText(getApplicationContext(), "Account created and verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Valodations on sign up form
    private boolean validateForm() {
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("Required.");
            return false;
        } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            edtPassword.setError("Required.");
            return false;
        }else {
            edtEmail.setError(null);
            edtPassword.setError(null);
            return true;
        }
    }
}