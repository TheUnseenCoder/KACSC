package com.advance.kacsc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderItemViewHolder> {
    private Context context;
    private List<OrderItem> orderItemList;

    public OrderAdapter(Context context, List<OrderItem> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItemList.get(position);
        holder.bind(orderItem);
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImageView;
        private TextView productNameTextView;
        private TextView variantTextView;
        private TextView sizeTextView;
        private TextView quantityTextView;
        private TextView PaymentStatusTextView;
        private TextView OrderStatusTextView;
        private ImageView viewButton;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image);
            productNameTextView = itemView.findViewById(R.id.product_name_text_view);
            variantTextView = itemView.findViewById(R.id.variant_text_view);
            sizeTextView = itemView.findViewById(R.id.size_text_view);
            quantityTextView = itemView.findViewById(R.id.quantity_text_view);
            PaymentStatusTextView = itemView.findViewById(R.id.PaymentStatus);
            OrderStatusTextView = itemView.findViewById(R.id.OrderStatus);
            viewButton = itemView.findViewById(R.id.btn_view);
        }

        public void bind(OrderItem orderItem) {
            byte[] imageData = Base64.decode(orderItem.getImagePath(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            productImageView.setImageBitmap(bitmap);
            productNameTextView.setText(orderItem.getProductName());
            sizeTextView.setText("Size: " + orderItem.getSize());
            quantityTextView.setText("Quantity: " + String.valueOf(orderItem.getQuantity()));

            OrderStatusTextView.setText(orderItem.getOrderStatus());
            if (orderItem.getOrderStatus().equals("Pending")) {
                OrderStatusTextView.setTextColor(Color.parseColor("#FE9705"));
            } else if (orderItem.getOrderStatus().equals("Ready for Pick Up")) {
                OrderStatusTextView.setTextColor(Color.parseColor("#0569FF"));
            } else if (orderItem.getOrderStatus().equals("Approved") || orderItem.getOrderStatus().equals("Paid")) {
                OrderStatusTextView.setTextColor(Color.parseColor("#3AC430"));
            }

            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        OrderItem clickedItem = orderItemList.get(position);
                        Integer productIdString = clickedItem.getProductId();
                        String email = clickedItem.getEmail();
                        Integer order_id = clickedItem.getId();
                        String size = clickedItem.getSize();
                        Integer quantity = clickedItem.getQuantity();
                        String product_name = clickedItem.getProductName();
                        String product_description = clickedItem.getProductDescription();
                        String base_price = String.valueOf(clickedItem.getBasePrice());
                        Double total = clickedItem.getTotal();
                        String order_status = clickedItem.getOrderStatus();
                        String dedication = clickedItem.getDedication();
                        String date = clickedItem.getDate();
                        String time = clickedItem.getTime();


                        Intent intent = new Intent(context, OrderDetailsActivity.class);
                        intent.putExtra("EMAIL", email);
                        intent.putExtra("ORDER_ID", order_id);
                        intent.putExtra("PRODUCT_ID", productIdString);
                        intent.putExtra("QUANTITY", quantity);
                        intent.putExtra("SELECTED_SIZE", size);
                        intent.putExtra("PRODUCT_NAME", product_name);
                        intent.putExtra("PRODUCT_DESCRIPTION", product_description);
                        intent.putExtra("BASE_PRICE", base_price);
                        intent.putExtra("TOTAL", total);
                        intent.putExtra("ORDER_STATUS", order_status);
                        intent.putExtra("DEDICATION", dedication);
                        intent.putExtra("DATE", date);
                        intent.putExtra("TIME", time);

                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
