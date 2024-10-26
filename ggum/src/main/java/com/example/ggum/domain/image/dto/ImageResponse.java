package com.example.ggum.domain.image.dto;

import com.example.ggum.domain.image.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private Long id;
    private String imageName;

    public ImageResponse(Image image) {
        this.id = image.getId();
        this.imageName = image.getImageName();
    }
}
