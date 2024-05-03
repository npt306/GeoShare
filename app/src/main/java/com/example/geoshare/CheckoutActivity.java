package com.example.geoshare;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity {
    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;
    private FirebaseFunctions functions;
    private String customerId = "cus_Px7y5UbdbslNKO";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        functions = FirebaseFunctions.getInstance();

        initializeStripePaymentConfiguration();

        initializePaymentSheet();

        fetchOrCreateStripeCustomer();
    }

    private void initializeStripePaymentConfiguration() {
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51OM54hL1bECNnFcvEJXdx2V7gJ6RxaDq7WVV1Jw3UZydI3Cag3lrQLQFwbadyM7Rp5uj8LdRXBSlAS0x5cJVuxuc00Yx8eUBEk"
        );
    }

    private void initializePaymentSheet() {
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
    }

    private void fetchOrCreateStripeCustomer() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("stripe_customers").document(currentUserUid).get()
                .addOnSuccessListener(document -> {
                    customerId = document.getString("customer_id");
                    String productId = getIntent().getStringExtra("productId");
                    if (productId == null) {
                        fetchPaymentIntentClientSecret();
                    } else {
                        createSubscription();
                    }
                })
                .addOnFailureListener(e -> createStripeCustomer(currentUserUid));
    }

    private void createStripeCustomer(String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());

        functions.getHttpsCallable("createStripeCustomer").call(data)
                .addOnSuccessListener(result -> {
                    Map<String, Object> customer = (Map<String, Object>) result.getData();
                    customerId = (String) customer.get("id");
                    storeStripeCustomerId(userId, customerId);
                })
                .addOnFailureListener(e -> {
                    Log.w("CheckoutActivity", "Error creating customer", e);
                    Toast.makeText(
                            this,
                            "Failed to create a new customer.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void storeStripeCustomerId(String userId, String customerId) {
        FirebaseFirestore.getInstance().collection("stripe_customers").document(userId)
                .set(Map.of("customer_id", customerId));
    }

    private void fetchPaymentIntentClientSecret() {
        float amount = Float.parseFloat(getIntent().getStringExtra("totalAmount")) * 100;
        String currency = "usd";

        Map<String, Object> data = new HashMap<>();
        data.put("amount", (long) amount);
        data.put("currency", currency);
        data.put("customerId", customerId);

        functions
                .getHttpsCallable("createPaymentIntent")
                .call(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                        paymentIntentClientSecret = (String) result.get("clientSecret");
                        configurePaymentSheet();
                    } else {
                        Toast.makeText(
                                this,
                                "Failed to fetch payment intent secret.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void createSubscription() {
        String priceId = getIntent().getStringExtra("priceId");

        Map<String, Object> data = new HashMap<>();
        data.put("customerId", "cus_Px7y5UbdbslNKO");
        data.put("priceId", priceId);

        functions.getHttpsCallable("createSubscription").call(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                        paymentIntentClientSecret = (String) result.get("clientSecret");
                        configurePaymentSheet();
                    } else {
                        Toast.makeText(
                                this,
                                "Failed to create subscription.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void configurePaymentSheet() {
        if (paymentIntentClientSecret != null) {
            PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("GeoShare");
            paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
        }
    }

    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment succeeded", Toast.LENGTH_LONG).show();
            handleSuccessfulPayment();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment canceled", Toast.LENGTH_LONG).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            // PaymentSheetResult.Failed includes an error which can be used to display more details
            PaymentSheetResult.Failed failedResult = (PaymentSheetResult.Failed) paymentSheetResult;
            Toast.makeText(
                    this,
                    "Payment failed: " + failedResult.getError().getLocalizedMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void handleSuccessfulPayment() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        firebaseDatabase.getReference("Premium").child(userId).setValue(true);
        finish();
    }

    private String getTAG() {
        return "CheckoutActivity";
    }
}
