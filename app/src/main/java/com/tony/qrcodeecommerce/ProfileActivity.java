package com.tony.qrcodeecommerce;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tony.qrcodeecommerce.utils.Profile;
import com.tony.qrcodeecommerce.utils.ProfileSP;

public class ProfileActivity extends Activity {

    private ProfileSP profileSP;

    private EditText profileName,profilePhone,profileEmail;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileName = (EditText) findViewById(R.id.profile_name);
        profilePhone = (EditText)findViewById(R.id.profile_phone);
        profileEmail = (EditText)findViewById(R.id.profile_email);
        submit = (Button) findViewById(R.id.submit);

        profileSP = new ProfileSP(getApplicationContext());

        profileName.setText(profileSP.getUserProfile().getStuName());
        profilePhone.setText(profileSP.getUserProfile().getStuPhone());
        profileEmail.setText(profileSP.getUserProfile().getStuEmail());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileSP.setUserProfile(getEditTextProfile());
                Toast.makeText(getApplicationContext(),"修改完成！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Profile getEditTextProfile() {
        return new Profile(MainApplication.getLoginUserId(),
                profileName.getText().toString(),
                profilePhone.getText().toString(),
                profileEmail.getText().toString());
    }
}
