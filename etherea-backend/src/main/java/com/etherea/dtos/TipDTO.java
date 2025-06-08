package com.etherea.dtos;

import com.etherea.models.Tip;

import java.time.LocalDateTime;
public class TipDTO {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String image;
    private LocalDateTime dateCreation;
    public TipDTO() {}
    public TipDTO(Long id, String title, String description, String content, String image, LocalDateTime dateCreation) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.image = image;
        this.dateCreation = dateCreation;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    public static TipDTO fromTip(Tip tip) {
        return new TipDTO(
                tip.getId(),
                tip.getTitle(),
                tip.getDescription(),
                tip.getContent(),
                tip.getImage(),
                tip.getDateCreation()
        );
    }
    public Tip toTip() {
        Tip tip = new Tip();
        tip.setId(this.id);
        tip.setTitle(this.title);
        tip.setDescription(this.description);
        tip.setContent(this.content);
        tip.setImage(this.image);
        tip.setDateCreation(this.dateCreation);
        return tip;
    }
}
