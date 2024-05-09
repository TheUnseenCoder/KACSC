package com.advance.kacsc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.OrderRequest;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderDetailsActivity extends AppCompatActivity {
    private double totalAdditional = 0.0;
    private double totalAmount = 0.0;
    private double total = 0.0;
    private double remaining_balance = 0.0;
    private int orderId;
    private static final int PAYPAL_REQUEST_CODE = 123;
    private String buyerEmail;
    private String dedication, date, time;

    private int productId;
    private int quantity;
    private String size;
    private double basePrice;
    double PercentageValue = 0.7; // Default to 70%

    private static final String TAG = "MyTag";
    PaymentButtonContainer paymentButtonContainer100, paymentButtonContainer70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

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
        dedication = intent.getStringExtra("DEDICATION");
        date = intent.getStringExtra("DATE");
        time = intent.getStringExtra("TIME");
        orderId = intent.getIntExtra("ORDER_ID", 0);
        productId = intent.getIntExtra("PRODUCT_ID", 0);
        buyerEmail = intent.getStringExtra("EMAIL");
        quantity = intent.getIntExtra("QUANTITY", 0);
        total = intent.getDoubleExtra("TOTAL", 0.00);
        size = intent.getStringExtra("SELECTED_SIZE");
        String productName = intent.getStringExtra("PRODUCT_NAME");
        String productDescription = intent.getStringExtra("PRODUCT_DESCRIPTION");
        String basePriceString = intent.getStringExtra("BASE_PRICE"); // Retrieve as String
        String order_status = intent.getStringExtra("ORDER_STATUS"); // Retrieve as String


        TextView productNameTextView = findViewById(R.id.textViewProductName);
        TextView productDescriptionTextView = findViewById(R.id.textViewProductDescription);
        TextView basePriceTextView = findViewById(R.id.baseprice);
        TextView qtyTextView = findViewById(R.id.qty);
        TextView sizeTextView = findViewById(R.id.size);
        TextView dedicationTextView = findViewById(R.id.dedication);
        TextView dateTextView = findViewById(R.id.date);
        TextView timeTextView = findViewById(R.id.time);

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
        fetchImage(productId);

        TextView OrderStatusTextView = findViewById(R.id.order_status);
        OrderStatusTextView.setText("Order Status: " + order_status);
        Button orderReceived = findViewById(R.id.order_received);
        orderReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus(orderId);
            }
        });
        Button cancelorderReceived = findViewById(R.id.cancel_order);
        cancelorderReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatusCancel(orderId);
            }
        });

        if (order_status.equals("Pending")) {
            OrderStatusTextView.setTextColor(Color.parseColor("#FE9705"));
        } else if (order_status.equals("Ready for pick up")) {
            OrderStatusTextView.setTextColor(Color.parseColor("#0569FF"));
        } else {
            OrderStatusTextView.setTextColor(Color.parseColor("#3AC430"));
        }


        TextView paid_amount = findViewById(R.id.amountpaid);
        paid_amount.setText("Total Amount to be pay: ₱" + total);

        if(order_status.equals("Approved") || order_status.equals("Pending")){
            Button cancelOrderBtn = findViewById(R.id.cancel_order);
            cancelOrderBtn.setVisibility(View.VISIBLE);
        }else{
            Button cancelOrderBtn = findViewById(R.id.cancel_order);
            cancelOrderBtn.setVisibility(View.GONE);
        }

        if(order_status.equals("Ready for Pick Up")){
            Button orderReceivedBtn = findViewById(R.id.order_received);
            orderReceivedBtn.setVisibility(View.VISIBLE);
        }else{
            Button orderReceivedBtn = findViewById(R.id.order_received);
            orderReceivedBtn.setVisibility(View.GONE);
        }

        if(order_status.equals("Approved")){
            LinearLayout percent70Layout = findViewById(R.id.percent70Layout);
            percent70Layout.setVisibility(View.VISIBLE);
        }else{
            LinearLayout percent70Layout = findViewById(R.id.percent70Layout);
            percent70Layout.setVisibility(View.GONE);
        }

        paymentButtonContainer70 = findViewById(R.id.payment_button_container_70);
        paymentButtonContainer70.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        Log.d(TAG, "create: ");
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value(String.valueOf(total))
                                                        .build()
                                        )
                                        .build()
                        );
                        OrderRequest order = new OrderRequest(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                // Handle capture completion
                                // Accessing the transaction ID (payment_id) directly from the captures array
                                String resultString = String.format("CaptureOrderResult: %s", result);
                                // Define a regular expression pattern to match the id
                                Pattern pattern = Pattern.compile("Capture\\(id=(\\w+),");
                                // Create a matcher with the input string
                                Matcher matcher = pattern.matcher(resultString);
                                if (matcher.find()) {
                                    String transactionId = matcher.group(1);
                                    // Further processing with the transaction ID
                                    Toast.makeText(OrderDetailsActivity.this, "Successfully Paid", Toast.LENGTH_SHORT).show();

                                    Log.e("Transaction ID: ", transactionId);
                                    String payerId = String.format("%s", approval.getData().getPayerId());
                                    String payerPaymentEmail1 = String.format("%s", approval.getData().getPayer().getEmail());
                                    String payerGivenName = String.format("%s", approval.getData().getPayer().getName().getGivenName());
                                    String payerFamilyName = String.format("%s", approval.getData().getPayer().getName().getFamilyName());
                                    String payerPaymentEmail = payerPaymentEmail1.substring(payerPaymentEmail1.indexOf("=") + 1, payerPaymentEmail1.indexOf(",")).trim();

                                    // Send order details to server based on payment percent
                                    sendOrderDetailsToServer(orderId, date, time, buyerEmail, productId, size, dedication, total, quantity, basePrice, payerId, transactionId, payerPaymentEmail, payerGivenName, payerFamilyName);
                                } else {
                                    // Handle case when no match is found
                                    Log.e("Transaction ID: ", "No match found");
                                    Toast.makeText(OrderDetailsActivity.this, "Failed to process transaction ID", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
        );
    }

    // Define a method to fetch the image using Volley
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

    private void sendOrderDetailsToServer(Integer orderId, String date, String time,String buyerEmail, Integer productId, String size, String dedication, Double total, Integer quantity, Double basePrice, String payerId, String transactionId, String payerPaymentEmail, String payerGivenName, String payerFamilyName) {
        // Example:
        String url = "http://192.168.1.11/KACSC/includes/pay_again.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle server response
                        Log.e("Response from server", response); // Log the response

                        if (response.trim().equalsIgnoreCase("success")) {
                            // Order placed successfully
                            Toast.makeText(OrderDetailsActivity.this, "Order is successfully paid", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OrderDetailsActivity.this, Orders.class);

                            startActivity(intent);
                        } else {
                            // Order placement failed
                            Toast.makeText(OrderDetailsActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(OrderDetailsActivity.this, "Failed to place order. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Add parameters for the order details
                params.put("email", buyerEmail);
                params.put("product_id", String.valueOf(productId));
                params.put("quantity", String.valueOf(quantity));
                params.put("size", size);
                params.put("dedication", dedication);
                params.put("base_price", String.valueOf(basePrice));
                params.put("total", String.valueOf(total));
                params.put("transaction_id", transactionId);
                params.put("payer_id", payerId);
                params.put("payer_givenname", payerGivenName);
                params.put("payer_familyname", payerFamilyName);
                params.put("payer_email", payerPaymentEmail);
                params.put("date", date);
                params.put("time", time);
                params.put("order_id", String.valueOf(orderId));


                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void updateOrderStatus(int orderId) {
        // Example:
        String url = "http://192.168.1.11/KACSC/includes/update_orderstatus.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle server response
                        Log.e("Response from server", response); // Log the response

                        if (response.trim().equalsIgnoreCase("success")) {
                            // Order placed successfully
                            Toast.makeText(OrderDetailsActivity.this, "Order has been received successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OrderDetailsActivity.this, Orders.class);
                            startActivity(intent);
                            Log.e("Order ID", String.valueOf(orderId));
                        } else {
                            // Order placement failed
                            Toast.makeText(OrderDetailsActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(OrderDetailsActivity.this, "Failed to place order. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("order_id", String.valueOf(orderId));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void updateOrderStatusCancel(int orderId) {
        // Example:
        String url = "http://192.168.1.11/KACSC/includes/update_orderstatuscancel.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle server response
                        Log.e("Response from server", response); // Log the response

                        if (response.trim().equalsIgnoreCase("success")) {
                            // Order placed successfully
                            Toast.makeText(OrderDetailsActivity.this, "Order status successfully cancelled!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OrderDetailsActivity.this, Orders.class);
                            startActivity(intent);
                            Log.e("Order ID", String.valueOf(orderId));
                        } else {
                            // Order placement failed
                            Toast.makeText(OrderDetailsActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(OrderDetailsActivity.this, "Failed to place order. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("order_id", String.valueOf(orderId));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
