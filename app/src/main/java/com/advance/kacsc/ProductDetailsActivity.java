package com.advance.kacsc;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProductDetailsActivity extends AppCompatActivity {

    private List<String> sizesList = new ArrayList<>();
    private CalendarView calendarView;
    private String selectedTime;
    private String additional;
    private Integer quantity;

    private static final String TAG = "MyTag";
    private String buyerEmail;
    private int productID;
    private String selectedDate;
    private Double basePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize variants container

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

        String productIdString = getIntent().getStringExtra("PRODUCT_ID");
        buyerEmail = SharedPreferencesUtils.getStoredEmail(this);
        productID = Integer.parseInt(getIntent().getStringExtra("PRODUCT_ID"));


        // Check if productIdString is not nullProdu
        if (productIdString != null) {
            try {
                // Parse the productIdString to an int
                int productId = Integer.parseInt(productIdString);

                // Fetch product details, sizes, and variants
                fetchProductDetails(productId);
            } catch (NumberFormatException e) {
                // Handle the case where productIdString cannot be parsed to an int
                Toast.makeText(this, "Invalid product ID format", Toast.LENGTH_SHORT).show();
                // Optionally, close the activity or take appropriate action
                finish();
            }
        } else {
            // Handle the case where productIdString is null
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
            // Optionally, close the activity or take appropriate action
            finish();
        }



        calendarView = findViewById(R.id.calendarView);

        // Disable past dates and dates before 1 week from today
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 0);
        minDate.add(Calendar.DAY_OF_MONTH, 7);
        calendarView.setMinDate(minDate.getTimeInMillis());

        // Set today's date as selected
        calendarView.setDate(Calendar.getInstance().getTimeInMillis(), false, true);

        EditText additionalEditText = findViewById(R.id.additionalstext);
        additional = additionalEditText.getText().toString();

        Button timeButton = findViewById(R.id.time_button);
        timeButton.setOnClickListener(v -> showTimePicker());

        Button favorite = findViewById(R.id.favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFav(storedEmail, productID);
            }
        });

        Button BuyNow = findViewById(R.id.buttonBuyNow);
        BuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Quantity = findViewById(R.id.quantitytext);
                // Retrieve the quantity from the EditText
                String quantityText = Quantity.getText().toString().trim();
                if (quantityText.isEmpty()) {
                    // Display a toast to prompt the user to fill the quantity field
                    Toast.makeText(ProductDetailsActivity.this, "Please enter a quantity", Toast.LENGTH_SHORT).show();
                    return; // Exit the method without sending the order details
                }
                try {
                    // Parse the quantity text to an integer
                    quantity = Integer.valueOf(quantityText);
                } catch (NumberFormatException e) {
                    // Handle the case where quantityText cannot be parsed to an integer
                    Toast.makeText(ProductDetailsActivity.this, "Invalid quantity format", Toast.LENGTH_SHORT).show();
                    return; // Exit the method without sending the order details
                }

                // Check if any required fields are null or empty
                if (selectedDate == null || selectedDate.isEmpty() || selectedTime == null || selectedTime.isEmpty() || quantity == null) {
                    // Display a toast to prompt the user to fill all required fields
                    Toast.makeText(ProductDetailsActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                    return; // Exit the method without sending the order details
                }
                // Send order details to the server
                sendOrderDetailsToServer(additional, basePrice, storedEmail, productID, selectedTime, selectedDate, quantity);
            }
        });



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Called when the user changes the selected date
                // Construct the selected date in the desired format (e.g., YYYY-MM-DD)
                String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                // Store the selected date in the selectedDate variable
                selectedDate = formattedDate;
                // Optionally, you can perform further actions here based on the selected date
            }
        });


    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            // Handle the selected time
            Button timeButton = findViewById(R.id.time_button);
            selectedTime = selectedHour + ":" + selectedMinute;
            timeButton.setText(selectedTime);
            // Update the UI or perform other actions
        }, hour, minute, false);
        timePickerDialog.show();
    }
    private void fetchProductDetails(int productId) {
        String url = "http://192.168.1.11/KACSC/includes/product_details.php?product_id=" + productId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject productDetails = response.getJSONObject("product_details");

                            // Display product name and description
                            TextView productNameTextView = findViewById(R.id.textViewProductName);
                            TextView productDescriptionTextView = findViewById(R.id.textViewProductDescription);
                            TextView productPrice = findViewById(R.id.baseprice);
                            TextView productSize = findViewById(R.id.inclusiontext);
                            TextView productFlavors = findViewById(R.id.freebiestext);

                            productNameTextView.setText(productDetails.getString("product_name"));
                            productDescriptionTextView.setText(productDetails.getString("product_description"));
                            productPrice.setText("₱" + productDetails.getString("base_price"));
                            productSize.setText(productDetails.getString("size"));
                            basePrice = productDetails.getDouble("base_price");
                            String flavors = productDetails.getString("flavors");
                            productFlavors.setText(flavors);

                            byte[] imageData = Base64.decode(productDetails.getString("image"), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                            ImageView imageView = findViewById(R.id.images_container); // Update with correct ImageView ID
                            imageView.setImageBitmap(bitmap);
                            // Display images
//                            displayImages(productDetails.getJSONArray("images"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void addToFav(String email, Integer product_id) {
        // Example:
        String url = "http://192.168.1.11/KACSC/includes/addtofav.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle server response
                        if (response.trim().equalsIgnoreCase("success")) {
                            // Order placed successfully
                            Toast.makeText(ProductDetailsActivity.this, "Product added to favorites successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Order placement failed
                            Toast.makeText(ProductDetailsActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(ProductDetailsActivity.this, "Failed to place order. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Add parameters for the order details
                params.put("email", email);
                params.put("product_id", String.valueOf(product_id));

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

    private void sendOrderDetailsToServer(String additional, Double basePrice, String buyer_email, int product_id, String selectedTime, String selectedDate, Integer quantity) {
        // Example:
        String url = "http://192.168.1.11/KACSC/includes/buynow.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle server response
                        if (response.trim().equalsIgnoreCase("success")) {
                            // Order placed successfully
                            Toast.makeText(ProductDetailsActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProductDetailsActivity.this, CategoryList.class);
                            startActivity(intent);
                        } else {
                            // Order placement failed
                            Toast.makeText(ProductDetailsActivity.this, response, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Server response: " + response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(ProductDetailsActivity.this, "Failed to place order. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Add parameters for the order details
                params.put("base_price", String.valueOf(basePrice));
                params.put("email", buyer_email);
                params.put("product_id", String.valueOf(product_id));
                params.put("time", selectedTime);
                params.put("date", selectedDate);
                params.put("dedication", additional);
                params.put("quantity" , String.valueOf(quantity));

                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

}

