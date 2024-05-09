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

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryItemViewHolder> {
    private Context context;
    private List<OrderHistoryItem> orderHistoryItemList;

    public OrderHistoryAdapter(Context context, List<OrderHistoryItem> orderHistoryItemList) {
        this.context = context;
        this.orderHistoryItemList = orderHistoryItemList;
    }

    @NonNull
    @Override
    public OrderHistoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_history_item_layout, parent, false);
        return new OrderHistoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryItemViewHolder holder, int position) {
        OrderHistoryItem orderHistoryItem = orderHistoryItemList.get(position);
        holder.bind(orderHistoryItem);
    }

    @Override
    public int getItemCount() {
        return orderHistoryItemList.size();
    }

    public class OrderHistoryItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImageView;
        private TextView productNameTextView;
        private TextView variantTextView;
        private TextView sizeTextView;
        private TextView quantityTextView;
        private TextView PaymentStatusTextView;
        private TextView OrderStatusTextView;
        private ImageView viewButton;

        public OrderHistoryItemViewHolder(@NonNull View itemView) {
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

        public void bind(OrderHistoryItem orderHistoryItem) {
            byte[] imageData = Base64.decode(orderHistoryItem.getImagePath(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            productImageView.setImageBitmap(bitmap);
            productNameTextView.setText(orderHistoryItem.getProductName());
            sizeTextView.setText("Size: " + orderHistoryItem.getSize());
            quantityTextView.setText("Quantity: " + String.valueOf(orderHistoryItem.getQuantity()));

            OrderStatusTextView.setText(orderHistoryItem.getOrderStatus());
            if (orderHistoryItem.getOrderStatus().equals("Order Received")) {
                OrderStatusTextView.setTextColor(Color.parseColor("#3AC430"));
            } else {
                OrderStatusTextView.setTextColor(Color.parseColor("#E53935"));
            }

            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        OrderHistoryItem clickedItem = orderHistoryItemList.get(position);
                        Integer productIdString = clickedItem.getProductId();
                        String email = clickedItem.getEmail();
                        Integer order_id = clickedItem.getId();
                        String size = clickedItem.getSize();
                        Integer quantity = clickedItem.getQuantity();
                        String product_name = clickedItem.getProductName();
                        String product_description = clickedItem.getProductDescription();
                        String base_price = String.valueOf(clickedItem.getBasePrice());
                        String first_transaction_id = clickedItem.getFirstTransactionId();
                        Double amount_paid = clickedItem.getAmount_paid();
                        String order_status = clickedItem.getOrderStatus();
                        String dedication = clickedItem.getDedication();
                        String delivery_date = clickedItem.getDeliverydate();
                        String delivery_time = clickedItem.getDeliverytime();

                        ArrayList<String> images = new ArrayList<>();
                        images.add(clickedItem.getImagePath());

                        Intent intent = new Intent(context, OrderHistoryDetailsActivity.class);
                        intent.putExtra("EMAIL", email);
                        intent.putExtra("ORDER_ID", order_id);
                        intent.putExtra("PRODUCT_ID", productIdString);
                        intent.putExtra("QUANTITY", quantity);
                        intent.putExtra("SELECTED_SIZE", size);
                        intent.putExtra("PRODUCT_NAME", product_name);
                        intent.putExtra("PRODUCT_DESCRIPTION", product_description);
                        intent.putExtra("BASE_PRICE", base_price);
                        intent.putExtra("IMAGES", images);
                        intent.putExtra("DEDICATION", dedication);
                        intent.putExtra("AMOUNT_PAID", amount_paid);
                        intent.putExtra("DELIVERY_DATE", delivery_date);
                        intent.putExtra("ORDER_STATUS", order_status);
                        intent.putExtra("FIRST_TRANSACTION_ID", first_transaction_id);
                        intent.putExtra("DELIVERY_TIME", delivery_time);


                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
