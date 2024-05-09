package com.advance.kacsc;

import static com.android.volley.VolleyLog.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AddToCartAdapter extends RecyclerView.Adapter<AddToCartAdapter.CartItemViewHolder> {
    private RequestQueue requestQueue;
    private Context context;
    private List<CartItem> cartItemList;


    public AddToCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorites_item_layout, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImageView;
        private TextView productNameTextView, productDescriptionTextView;

        private TextView basePriceTextView;
        private ImageView deleteButton, buyNowButton;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name_text_view);
            basePriceTextView = itemView.findViewById(R.id.base_price);
            productDescriptionTextView = itemView.findViewById(R.id.product_description_text_view);

            deleteButton = itemView.findViewById(R.id.btn_Delete);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        showDeleteConfirmationDialog(currentPosition);
                    }
                }
            });

            buyNowButton = itemView.findViewById(R.id.btn_buynow);
            buyNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        CartItem clickedItem = cartItemList.get(currentPosition);
                        ArrayList<String> images = new ArrayList<>();
                        images.add(clickedItem.getImagePath());
                        Integer productId = clickedItem.getProductId();
                        // Convert the productId to a String
                        String productIdString = String.valueOf(productId);
                        // Ensure that the context used to start the activity is valid
                        if (context != null) {
                            Intent intent = new Intent(context, ProductDetailsActivity.class);
                            intent.putExtra("PRODUCT_ID", productIdString);
                            context.startActivity(intent);
                        } else {
                            Log.e(TAG, "Context is null. Cannot start ProductDetailsActivity.");
                        }
                    }

                }
            });
        }

        private void showDeleteConfirmationDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Item");
            builder.setMessage("Are you sure you want to delete this item from the cart?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int itemId = cartItemList.get(position).getId();
                    deleteCartItem(itemId, position);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                }
            });
            builder.create().show();
        }

        private void deleteCartItem(int itemId, final int position) {
            String url = "http://192.168.1.11/KACSC/includes/favorites_remove.php?id=" + itemId;
            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            cartItemList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Product has been remove from favorites", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Removing Error", "Error removing item", error);
                            Toast.makeText(context, "Error removing item", Toast.LENGTH_SHORT).show();
                        }
                    });
            requestQueue.add(request);
        }

        public void bind(CartItem cartItem) {
            // Load image from BLOB data
            byte[] imageData = Base64.decode(cartItem.getImagePath(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            productImageView.setImageBitmap(bitmap);
            productDescriptionTextView.setText(cartItem.getDescription());
            productNameTextView.setText(cartItem.getProductName());
            basePriceTextView.setText(String.valueOf(cartItem.base_price()));
        }
    }
}
