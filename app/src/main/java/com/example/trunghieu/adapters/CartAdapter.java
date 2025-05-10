package com.example.trunghieu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trunghieu.R;
import com.example.trunghieu.models.CartItem;
import com.example.trunghieu.models.Product;
import com.example.trunghieu.database.AppDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;
    private CartAdapterListener listener;

    public interface CartAdapterListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onRemoveItem(CartItem item);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartAdapterListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private TextView quantityTextView;
        private ImageButton decreaseButton;
        private ImageButton increaseButton;
        private ImageButton removeButton;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            removeButton = itemView.findViewById(R.id.removeButton);

            decreaseButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    CartItem item = cartItems.get(position);
                    if (item.getQuantity() > 1) {
                        listener.onQuantityChanged(item, item.getQuantity() - 1);
                    }
                }
            });

            increaseButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    CartItem item = cartItems.get(position);
                    listener.onQuantityChanged(item, item.getQuantity() + 1);
                }
            });

            removeButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRemoveItem(cartItems.get(position));
                }
            });
        }

        void bind(CartItem cartItem) {
            Product product = cartItem.getProduct();
            if (product == null) {
                product = getProductDetails(cartItem.getProductId());
            }
            if (product != null) {
                productName.setText(product.getName());
                productPrice.setText(String.format("$%.2f", product.getPrice()));
                quantityTextView.setText(String.valueOf(cartItem.getQuantity()));

                Glide.with(context)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(productImage);
            }
        }

        private Product getProductDetails(int productId) {
            return AppDatabase.getInstance(context)
                    .productDao()
                    .getProductById(productId);
        }
    }
} 