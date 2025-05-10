import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.trunghieu.R;
import com.example.trunghieu.models.CartItem;
import com.example.trunghieu.models.Product;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
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
        private TextView productName;
        private TextView productPrice;
        private TextView quantityTextView;
        private ImageView productImage;

        CartViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            productImage = itemView.findViewById(R.id.productImage);
        }

        void bind(CartItem cartItem) {
            Product product = cartItem.getProduct();
            if (product != null) {
                productName.setText(product.getName());
                productPrice.setText(String.format("$%.2f", product.getPrice()));
                quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
                // Hiển thị mô tả sản phẩm nếu có view
                TextView productDescription = itemView.findViewById(R.id.productDescription);
                if (productDescription != null) {
                    productDescription.setText(product.getDescription());
                }
                Glide.with(context)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(productImage);
            }
        }
    }
} 