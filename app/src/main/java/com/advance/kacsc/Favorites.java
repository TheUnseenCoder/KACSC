package com.advance.kacsc;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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

public class Favorites extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AddToCartAdapter adapter;
    private List<CartItem> cartItemList;
    private RequestQueue requestQueue;
    private int id;
    private String email;
    private int productId;
    private String size;
    private String variant;
    private int quantity;
    private double base_price;
    private String productName;
    private String imagePath;
    private double additional;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);

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

        cartItemList = new ArrayList<>();
        adapter = new AddToCartAdapter(this, cartItemList);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchCartItems(storedEmail);

    }

    private void fetchCartItems(String email) {
        String url = "http://192.168.1.11/KACSC/includes/fetch_favorites.php?email=" + email;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject cartItemJSON = response.getJSONObject(i);
                                int id = cartItemJSON.getInt("id");
                                String email = cartItemJSON.getString("email");
                                int productId = cartItemJSON.getInt("product_id");
                                double base_price = cartItemJSON.getDouble("base_price");
                                String productName = cartItemJSON.getString("product_name");
                                String imagePath = cartItemJSON.getString("image");
                                String description = cartItemJSON.getString("description");

                                cartItemList.add(new CartItem(id, email, productId, productName, imagePath, base_price, description));
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
                        Log.e("Volley Error", "Error fetching data", error);
                        Toast.makeText(Favorites.this, "0 result for Add to Cart data!", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }

}