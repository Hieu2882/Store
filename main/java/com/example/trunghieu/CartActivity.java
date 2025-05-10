private void updateCartItem(CartItem item, int newQuantity) {
    item.setQuantity(newQuantity);
    RetrofitClient.getInstance()
            .getApiService()
            .updateCartItem(item.getId(), item)
            .enqueue(new Callback<CartItem>() {
                @Override
                public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                    if (response.isSuccessful()) {
                        // Load lại giỏ hàng để cập nhật số lượng và tổng tiền
                        loadCartItems();
                    } else {
                        Toast.makeText(CartActivity.this, "Failed to update cart", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CartItem> call, Throwable t) {
                    Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
} 