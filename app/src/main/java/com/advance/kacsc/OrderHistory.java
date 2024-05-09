package com.advance.kacsc;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private List<OrderHistoryItem> orderHistoryItemList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderhistory);

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

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderHistoryItemList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(this, orderHistoryItemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchCartItems();

    }

    private void fetchCartItems() {
        String url = "http://192.168.1.11/KACSC/includes/fetch_orderhistory.php?email=" + SharedPreferencesUtils.getStoredEmail(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject orderHistoryItemJSON = response.getJSONObject(i);
                                int id = orderHistoryItemJSON.getInt("id");
                                String email = orderHistoryItemJSON.getString("email");
                                int productId = orderHistoryItemJSON.getInt("product_id");
                                String size = orderHistoryItemJSON.getString("size");
                                int quantity = orderHistoryItemJSON.getInt("quantity");
                                double base_price = orderHistoryItemJSON.getDouble("base_price");
                                String productName = orderHistoryItemJSON.getString("name");
                                String imagePath = orderHistoryItemJSON.getString("image");
                                String delivery_date = orderHistoryItemJSON.getString("delivery_date");
                                String delivery_time = orderHistoryItemJSON.getString("delivery_time");
                                String status_order = orderHistoryItemJSON.getString("status");
                                String productDescription = orderHistoryItemJSON.getString("description");
                                double amount_paid = orderHistoryItemJSON.getDouble("total");
                                String dedication = orderHistoryItemJSON.getString("dedication");
                                String first_transaction_id = orderHistoryItemJSON.getString("transaction_id");


                                productDescription = productDescription.replace("\r\n", " ");

                                Toast.makeText(OrderHistory.this, "Data fetched Successfully", Toast.LENGTH_SHORT).show();
                                orderHistoryItemList.add(new OrderHistoryItem(id, email, productId, size, quantity, productName, imagePath, base_price, delivery_date, delivery_time, status_order, productDescription, amount_paid, first_transaction_id, dedication));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OrderHistory.this, "0 Order History", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }
}
