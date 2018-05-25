package com.donald.crowdfunding.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.donald.crowdfunding.business.R;
import com.donald.crowdfunding.util.JsonParser;
import com.donald.crowdfunding.util.Util;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class Payment extends AppCompatActivity implements OnCardFormSubmitListener, CardEditText.OnCardTypeChangedListener {
    private static final int GROUP_LEN = 4;
    private static final int koboToNaira = 100;
    String paystack_public_key = "pk_test_0e5f98685a822c219423b8fbfefaddf47dca0c41";
    double amount = 0.0;
    KProgressHUD hud;
    private Util util;
    private AppCompatActivity activity = Payment.this;
    private SweetAlertDialog pDialog;

    private EditText mPaymentDescription;
    private TextView mAmount;

    private TextView mTextError;
    private EditText mEmail;

    private Button payNowBtn;
    private String paymentFor;
    private String email;


    String totalAmount, emailAddress, accountName;

    private Charge charge;
    private Transaction transaction;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static String PREFS_NAME = "mypre";

    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD,
            CardType.DISCOVER, CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB,
            CardType.MAESTRO, CardType.UNIONPAY};

    private SupportedCardTypesView mSupportedCardTypesView;

    protected CardForm mCardForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        util = new Util();
        preferences = getApplicationContext().getSharedPreferences(
                PREFS_NAME, Activity.MODE_PRIVATE);

        PaystackSdk.setPublicKey(paystack_public_key);

        mSupportedCardTypesView = findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);


        mCardForm = findViewById(R.id.card_form);
        mCardForm.cardRequired(true)
                .maskCardNumber(true)
                .maskCvv(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .mobileNumberRequired(false)
                .mobileNumberExplanation("Make sure SMS is enabled for this mobile number")
                .actionLabel(getString(R.string.purchase))
                .setup(this);
        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);

        // Failure to set FLAG_SECURE exposes your app to screenshots allowing other apps to steal card details
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);

        mAmount = findViewById(R.id.amountPayable);

        mTextError = findViewById(R.id.card_error);
        mEmail = findViewById(R.id.bt_payment_email);
        mPaymentDescription = findViewById(R.id.bt_payment_description);

        payNowBtn = findViewById(R.id.proceed);

        hud = KProgressHUD.create(Payment.this);

//        totalAmount = preferences.getString("price", "price");
//        emailAddress = preferences.getString("email", "email");
//        accountName = preferences.getString("firstName", "") + " " +
//                preferences.getString("lastName", "");
        totalAmount="100100";
        emailAddress = "donald@gmail.com";
        accountName = "Donald Ebuga";


        Log.d("accountName", ""+accountName);

        double totalAmount1 = Double.parseDouble(totalAmount);
        String totalAmount2 = ((String.valueOf(util.getFormatedAmount(totalAmount1))));

        mAmount.setText(getString(R.string.naira_symbol).concat(totalAmount2));
        mEmail.setText(emailAddress);

        //initialize sdk
        PaystackSdk.initialize(getApplicationContext());

        //set click listener
        payNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (util.isNetworkAvailable(activity)) {
                    try {
                        validateInputs();
                        startAFreshCharge(true);

                    } catch (Exception e) {
                        Payment.this.mTextError.setText(String.format("An " +
                                "error occurred while charging card"));
                    }
                } else {
                    Toast.makeText(activity, "No Internet connectivity",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void validateInputs() {
        paymentFor = mPaymentDescription.getText().toString().trim();

        if (TextUtils.isEmpty(paymentFor)) {
            mPaymentDescription.setError("Enter your payment description");
            return;
        }
        if (TextUtils.isEmpty(mCardForm.getCardNumber())) {
            mCardForm.setCardNumberError("Enter a valid card number");
            return;
        }
        if (TextUtils.isEmpty(mCardForm.getExpirationYear())) {
            mCardForm.setExpirationError("Enter your card expiry Year");
            return;
        }
        if (TextUtils.isEmpty(mCardForm.getExpirationMonth())) {
            mCardForm.setExpirationError("Enter your card expiry Month");
            return;
        }
        if (TextUtils.isEmpty(mCardForm.getCvv())) {
            mCardForm.setCvvError("Enter your Card Verification Value");
        }

    }

    private void startAFreshCharge(boolean local) {

        // initialize the charge
        charge = new Charge();
        charge.setCard(loadCardFromForm());


        pDialog = new SweetAlertDialog(Payment.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
        pDialog.getProgressHelper().setSpinSpeed(1.0f);
        pDialog.getProgressHelper().setBarWidth(6);
        pDialog.setTitleText("Processing Transaction...");
        pDialog.setCancelable(false);

        pDialog.show();

        if (local) {

            amount = Double.parseDouble(totalAmount);
            amount = amount * koboToNaira;

            int finalAmount = 0;
            finalAmount = (int) amount;

            email = mEmail.getText().toString().trim();

            charge.setAmount(finalAmount);
            charge.setEmail(email);
            charge.setReference("Ancapps_" + Calendar.getInstance().getTimeInMillis());
            try {
                charge.putCustomField("Charge from ", "Android device");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chargeCard();
        }
    }

    /**
     * Method to validate the form, and set errors on the edittexts.
     */
    private Card loadCardFromForm() {
        //validate fields
        Card card;

        String cardNum = mCardForm.getCardNumber().trim();

        //build card object with ONLY the number, update the other fields later
        card = new Card.Builder(cardNum, 0, 0, "").build();
        String cvc = mCardForm.getCvv().trim();
        //update the cvc field of the card
        card.setCvc(cvc);

        //validate expiry month;
        String sMonth = mCardForm.getExpirationMonth().trim();
        int month = 0;
        try {
            month = Integer.parseInt(sMonth);
        } catch (Exception ignored) {
        }

        card.setExpiryMonth(month);

        String sYear = mCardForm.getExpirationYear().trim();
        int year = 0;
        try {
            year = Integer.parseInt(sYear);
        } catch (Exception ignored) {
        }
        card.setExpiryYear(year);

        return card;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void chargeCard() {
        transaction = null;
        PaystackSdk.chargeCard(Payment.this, charge,
                new Paystack.TransactionCallback() {
                    // This is called only after transaction is successful
                    @Override
                    public void onSuccess(Transaction transaction) {
                        //hud.dismiss();
                        pDialog.dismissWithAnimation();

                        Payment.this.transaction = transaction;
                        mTextError.setText("Transaction Successful ");
                        new PostPayment().execute();
                        updateTextViews();
                    }

                    // This is called only before requesting OTP
                    // Save reference so you may send to server if
                    // error occurs with OTP
                    // No need to dismiss dialog
                    @Override
                    public void beforeValidate(Transaction transaction) {
                        Payment.this.transaction = transaction;

                        updateTextViews();
                    }

                    @Override
                    public void onError(Throwable error, Transaction transaction) {
                        // If an access code has expired, simply ask your server for a new one
                        // and restart the charge instead of displaying error
                        Payment.this.transaction = transaction;
                        if (error instanceof ExpiredAccessCodeException) {
                            Payment.this.startAFreshCharge(false);
                            Payment.this.chargeCard();
                            return;
                        }

                        pDialog.dismissWithAnimation();

                        if (transaction.getReference() != null) {
                            mTextError.setText(String.format("%s  concluded with error: %s %s",
                                    transaction.getReference(), error.getClass().getSimpleName(),
                                    error.getMessage()));
                            // new verifyOnServer().execute(transaction.getReference());
                        } else {
                            MDToast.makeText(Payment.this, error.getMessage(),
                                    MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                            mTextError.setText(String.format("Error: %s %s", error.getClass()
                                    .getSimpleName(), error.getMessage()));
                        }
                        updateTextViews();
                    }

                });
    }


    private void updateTextViews() {
        if (transaction.getReference() != null) {
            mTextError.setText(String.format("Reference: %s", transaction.getReference()));
        } else {
            mTextError.setText("No transaction");
        }
    }

    @Override
    public void onCardFormSubmit() {

    }

    @Override
    public void onCardTypeChanged(CardType cardType) {
        if (cardType == CardType.EMPTY) {
            mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

        } else {
            mSupportedCardTypesView.setSelected(cardType);
            Toast.makeText(this, "Card is invalid", Toast.LENGTH_SHORT).show();
        }
    }



    class PostPayment extends AsyncTask<String, Void, JSONObject> {
        private static final String PAYMENT_URL = "https://ancapbooking.herokuapp.com/shipper/payment";
        JsonParser jsonParser = new JsonParser();
        private ProgressDialog pDialog;
        String sendPaymentFor;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(Payment.this);
            pDialog.setMessage("Sending payment details...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            sendPaymentFor = mPaymentDescription.getText().toString().trim();
            String sendAmount = mAmount.getText().toString().trim();
            String sendEmail = mEmail.getText().toString().trim();

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("paymentFor", sendPaymentFor);
                params.put("AccountName", accountName);
                params.put("Amount", sendAmount);
                params.put("email", sendEmail);

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        PAYMENT_URL, "POST", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json) {

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            try {
                if (json != null && json.getInt("status") == 200) {
                    MDToast.makeText(Payment.this, "Payment Successful",
                            MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            editor = preferences.edit();
            editor.putString("refid", transaction.getReference());
            editor.putString("paymentFor", sendPaymentFor);
            editor.commit();

//            Intent paymentHistory = new Intent(Payment.this,
//                    BookedActivity.class);
//            paymentHistory.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(paymentHistory);
//            finish();

        }


    }

}
