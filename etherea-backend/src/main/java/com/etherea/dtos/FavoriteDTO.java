package com.etherea.dtos;

import com.etherea.models.Favorite;

public class FavoriteDTO {
    private Long favoriteId;
    private Long userId;
    private Long productId;
    public FavoriteDTO() {
    }
    public FavoriteDTO(Long favoriteId, Long userId, Long productId) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.productId = productId;
    }
    public Long getFavoriteId() {
        return favoriteId;
    }
    public void setFavoriteId(Long favoriteId) {
        this.favoriteId = favoriteId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public static FavoriteDTO fromFavorite(Favorite favorite) {
        return new FavoriteDTO(favorite.getId(), favorite.getUser().getId(), favorite.getProduct().getId());
    }
}
