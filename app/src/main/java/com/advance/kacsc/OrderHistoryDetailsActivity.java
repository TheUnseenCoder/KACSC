package com.advance.kacsc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class OrderHistoryDetailsActivity extends AppCompatActivity {
    private double totalAdditional = 0.0;
    private double totalAmount = 0.0;
    private double amount_paid = 0.0;
    private double remaining_balance = 0.0;
    private int orderId;
    private String buyerEmail;
    private int productId;
    private int quantity;
    private String size;
    private String date, time, dedication;
    private double basePrice;
    double PercentageValue = 0.7; // Default to 70%

    private static final String TAG = "MyTag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_details);

        FloatingActionButton fab = findViewById(R.id.fab);

        String email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            fab.setVisibility(View.GONE); // Hide the FAB
        } else {
            fab.setVisibility(View.VISIBLE); // Show the FAB
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExpandedMenuFragment expandedMenuFragment = new ExpandedMenuFragment();
                    expandedMenuFragment.show(getSupportFragmentManager(), "ExpandedMenuFragment");
                }
            });
        }

        String storedEmail = SharedPreferencesUtils.getStoredEmail(this);
        if (storedEmail == null || storedEmail.isEmpty()) {
            fab.setVisibility(View.GONE); // Hide the FAB
        } else {
            fab.setVisibility(View.VISIBLE); // Show the FAB
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExpandedMenuFragment expandedMenuFragment = new ExpandedMenuFragment();
                    expandedMenuFragment.show(getSupportFragmentManager(), "ExpandedMenuFragment");
                }
            });
        }

        Intent intent = getIntent();
        orderId = intent.getIntExtra("ORDER_ID", 0);
        productId = intent.getIntExtra("PRODUCT_ID", 0);
        buyerEmail = intent.getStringExtra("EMAIL");
        quantity = intent.getIntExtra("QUANTITY", 0);
        amount_paid = intent.getDoubleExtra("AMOUNT_PAID", 0.00);
        size = intent.getStringExtra("SELECTED_SIZE");
        String productName = intent.getStringExtra("PRODUCT_NAME");
        String productDescription = intent.getStringExtra("PRODUCT_DESCRIPTION");
        String basePriceString = intent.getStringExtra("BASE_PRICE"); // Retrieve as String
        String order_status = intent.getStringExtra("ORDER_STATUS"); // Retrieve as String
        String firstTransactionId = intent.getStringExtra("FIRST_TRANSACTION_ID"); // Retrieve as String
        dedication = intent.getStringExtra("DEDICATION"); // Retrieve as String
        date = intent.getStringExtra("DELIVERY_DATE"); // Retrieve as String
        time = intent.getStringExtra("DELIVERY_TIME"); // Retrieve as String

        TextView productNameTextView = findViewById(R.id.textViewProductName);
        TextView productDescriptionTextView = findViewById(R.id.textViewProductDescription);
        TextView basePriceTextView = findViewById(R.id.baseprice);
        TextView qtyTextView = findViewById(R.id.qty);
        TextView sizeTextView = findViewById(R.id.size);
        TextView dedicationTextView = findViewById(R.id.dedication);
        TextView dateTextView = findViewById(R.id.delivery_date);
        TextView timeTextView = findViewById(R.id.delivery_time);

        fetchImage(productId);

        SimpleDateFormat inputFormats = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        // Create a SimpleDateFormat object for formatting the date in the desired output format
        SimpleDateFormat outputFormats = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

        try {
            // Parse the input date string into a Date object
            Date datess = inputFormats.parse(date);

            // Format the parsed Date object to the desired output format
            String formattedDates = outputFormats.format(datess);

            // Set the formatted date to the TextView
            dateTextView.setText("Delivery Date: " + formattedDates);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle parsing error if necessary
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);

        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.US);

        try {
            // Parse the input time string into a Date object
            Date dates = inputFormat.parse(time);

            // Format the parsed Date object to the desired output format
            String formattedTime = outputFormat.format(dates);

            // Set the formatted time to the TextView
            timeTextView.setText("Delivery Time: " + formattedTime);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle parsing error if necessary
        }
        dedicationTextView.setText("Dedication:" + dedication);


        // Convert base price from string to double
        basePrice = 0.0;
        try {
            basePrice = Double.parseDouble(basePriceString.replace("₱", ""));
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Handle parse error if necessary
        }

        // Set the extracted values to the corresponding views
        productNameTextView.setText(productName);
        productDescriptionTextView.setText(productDescription);
        basePriceTextView.setText("Base Price: " + String.format("₱%.2f", basePrice));
        sizeTextView.setText("Size: " + size);
        qtyTextView.setText("Ordered Quantity: " + quantity);

        TextView OrderStatusTextView = findViewById(R.id.order_status);
        OrderStatusTextView.setText("Order Status: " + order_status);




        if (order_status.equals("Order Received")) {
            OrderStatusTextView.setTextColor(Color.parseColor("#FE9705"));
            TextView FirstTransactionId = findViewById(R.id.first_transaction_id);
            FirstTransactionId.setText("Transaction ID: " + firstTransactionId);
            TextView paid_amount = findViewById(R.id.amountpaid);
            paid_amount.setText("Amount Paid (100%): ₱" + amount_paid);
        }else {
            TextView paid_amount = findViewById(R.id.amountpaid);
            TextView FirstTransactionId = findViewById(R.id.first_transaction_id);
            paid_amount.setVisibility(View.GONE);
            FirstTransactionId.setVisibility(View.GONE);
            OrderStatusTextView.setTextColor(Color.parseColor("#E53935"));
        }




    }
    private void fetchImage(int productId) {
        // URL of your server endpoint to fetch the image
        String url = "http://192.168.1.11/KACSC/includes/fetch_image.php?product_id=" + productId;

        // Create a request using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response containing the image data
                        // Here, you can parse the response and decode the image data
                        // For simplicity, let's assume the response contains Base64 encoded image data
                        ImageView imageView = findViewById(R.id.images_container);
                        byte[] decodedBytes = Base64.decode(response, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                        // Set the decoded image to your ImageView
                        imageView.setImageBitmap(bitmap);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(getApplicationContext(), "Error fetching image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest);
    }

}
