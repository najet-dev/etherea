package com.etherea.dtos;

import com.etherea.models.CartItem;

public class CartItemDTO {

    private Long id;
    private int subTotal;
    private int total;
    private ProductDTO productDTO;

    public CartItemDTO() {
    }

    public CartItemDTO(Long id, int subTotal, int total, ProductDTO productDTO) {
        this.id = id;
        this.subTotal = subTotal;
        this.total = total;
        this.productDTO = productDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ProductDTO getProductDTO() {
        return productDTO;
    }
    public void setProductDTO(ProductDTO productDTO) {
        this.productDTO = productDTO;
    }
    public String getDescription() {
        return productDTO.getDescription();
    }
    public double getPrice() {
        return productDTO.getPrice();
    }
    public int getQuantity() {
        // Récupérer la quantité directement depuis l'objet ProductDTO
        return productDTO.getQuantity();
    }
    public String getImage() {
        return productDTO.getImage();
    }
    public static CartItemDTO fromCartItem(CartItem cartItem) {
        ProductDTO productDTO = ProductDTO.fromProduct(cartItem.getProduct());
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(cartItem.getId());
        cartItemDTO.setSubTotal(cartItem.getSubTotal());
        cartItemDTO.setTotal(cartItem.getTotal());
        cartItemDTO.setProductDTO(productDTO);
        String description = cartItemDTO.getDescription();
        double price = cartItemDTO.getPrice();
        int productQuantity = productDTO.getQuantity();
        String image = cartItemDTO.getImage();
        
        return cartItemDTO;
    }
}
