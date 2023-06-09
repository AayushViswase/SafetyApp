package com.example.safetyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class UpdateEmailActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail,userNewEmail,userPassword;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail,editTextPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Update Email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar =findViewById(R.id.progressBar);
        editTextPwd=findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail=findViewById(R.id.editText_update_email_new);
        textViewAuthenticated=findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail=findViewById(R.id.button_update_email);

        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //Set old email id on textview
        userOldEmail = firebaseUser != null ? firebaseUser.getEmail() : null;
        TextView textViewOldEmail=findViewById(R.id.textView_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if(firebaseUser==null){
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong User details not available", Toast.LENGTH_SHORT).show();

        }else{
            reAuthenticate(firebaseUser);
        }
    }

    @SuppressLint("SetTextI18n")
    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser=findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(v -> {
            userPassword=editTextPwd.getText().toString();

            if(TextUtils.isEmpty(userPassword)){
                Toast.makeText(UpdateEmailActivity.this, "Password is needed to continue", Toast.LENGTH_SHORT).show();
                editTextPwd.setError("Please enter your password for authentication");
                editTextPwd.requestFocus();
            }else{
                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential= EmailAuthProvider.getCredential(userOldEmail,userPassword);

                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UpdateEmailActivity.this, "Password has been verified "+"You can update now", Toast.LENGTH_SHORT).show();

                        //set TextView to show that user is authenticated
                        textViewAuthenticated.setText("You are authenticated.You can update you email now.");

                        //disable update email button
                        editTextNewEmail.setEnabled(true);
                        editTextPwd.setEnabled(true);
                        buttonVerifyUser.setEnabled(false);
                        buttonUpdateEmail.setEnabled(true);
                        //change color
                        buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,R.color.dark_reen));
                        buttonUpdateEmail.setOnClickListener(v1 -> {
                            userNewEmail=editTextNewEmail.getText().toString();
                            if(TextUtils.isEmpty(userNewEmail)){
                                Toast.makeText(UpdateEmailActivity.this, "New Email is required", Toast.LENGTH_SHORT).show();
                                editTextNewEmail.setError("Please enter new mail");
                                editTextNewEmail.requestFocus();
                            }else if(!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                Toast.makeText(UpdateEmailActivity.this, "Please enter new email", Toast.LENGTH_SHORT).show();
                                editTextNewEmail.setError("Valid email Required");
                                editTextNewEmail.requestFocus();
                            } else if (userOldEmail.matches(userNewEmail)) {
                                Toast.makeText(UpdateEmailActivity.this, "New Email cannot be same as old", Toast.LENGTH_SHORT).show();
                                editTextNewEmail.setError("Please enter new mail");
                                editTextNewEmail.requestFocus();

                            }
                            else{
                                progressBar.setVisibility(View.VISIBLE);
                                updateEmail(firebaseUser);
                            }
                        });
                    }else{
                        try {
                            throw Objects.requireNonNull(task.getException());
                        }catch(Exception e){
                            Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.updateEmail(userNewEmail).addOnCompleteListener(task -> {
                if(task.isComplete()){
                    //verification mail
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this, "Email has been updated.Please verify your new Email", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(UpdateEmailActivity.this,UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    }catch (Exception e){
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            });
    }

    //Create ActionBar Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    //When any menu is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(UpdateEmailActivity.this);
        }else if(id==R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if (id==R.id.menu_update_profile) {
            Intent intent=new Intent(UpdateEmailActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id==R.id.menu_update_email) {
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_change_password) {
            Intent intent = new Intent(UpdateEmailActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateEmailActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UpdateEmailActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(UpdateEmailActivity.this,MainActivity.class);
            //Clear stack tpo prevent user coming back to userProfile Activation back button
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {

            Toast.makeText(UpdateEmailActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

}