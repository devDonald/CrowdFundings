package com.donald.crowdfunding.business;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;

public class DonationAmt extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_amt);
    }

    public void showAlertDialogWithAutoDismiss() {

        new iOSDialogBuilder(DonationAmt.this)
                .setTitle("Booking Notification!")
                .setSubtitle("You can conveniently view your successful load bookings " +
                        "from payment history on the navigation drawer")
                .setBoldPositiveLabel(true)
                .setCancelable(false)

                .setPositiveListener("Ok", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }
}
