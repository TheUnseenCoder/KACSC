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

public class Orders extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<OrderItem> orderItemList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders);

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


        ImageView history = findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Orders.this, OrderHistory.class);
                startActivity(intent);

            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderItemList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderItemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchCartItems();


    }

    private void fetchCartItems() {
        String url = "http://192.168.1.11/KACSC/includes/fetch_orders.php?email=" + SharedPreferencesUtils.getStoredEmail(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject orderItemJSON = response.getJSONObject(i);
                                int id = orderItemJSON.getInt("id");
                                String email = orderItemJSON.getString("email");
                                int productId = orderItemJSON.getInt("product_id");
                                String size = orderItemJSON.getString("size");
                                int quantity = orderItemJSON.getInt("quantity");
                                double base_price = orderItemJSON.getDouble("price");
                                String productName = orderItemJSON.getString("product_name");
                                String imagePath = orderItemJSON.getString("image");
                                String status_order = orderItemJSON.getString("status");
                                String productDescription = orderItemJSON.getString("description");
                                String dedication = orderItemJSON.getString("dedication");
                                double total = orderItemJSON.getDouble("total");
                                String date = orderItemJSON.getString("delivery_date");
                                String time = orderItemJSON.getString("delivery_time");


                                productDescription = productDescription.replace("\r\n", " ");

                                Toast.makeText(Orders.this, "Data fetched Successfully", Toast.LENGTH_SHORT).show();
                                orderItemList.add(new OrderItem(id, email, productId, size, quantity, productName, imagePath, base_price, status_order, productDescription, total, dedication, date, time));
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
                        Toast.makeText(Orders.this, "0 result for current order found", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }
}
