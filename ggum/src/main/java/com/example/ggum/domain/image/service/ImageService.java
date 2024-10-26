package com.example.ggum.domain.image.service;

import com.example.ggum.domain.image.entity.Image;
import com.example.ggum.domain.image.repository.ImageRepository;
import com.example.ggum.domain.image.utils.S3Uploader;
import com.example.ggum.domain.post.entity.Post;
import com.example.ggum.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Uploader s3Uploader;
    private final ImageRepository imageRepository;
    private final PostRepository postRepository;

    @Transactional
    public List<Image> uploadImages(Long postId, List<MultipartFile> images) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        List<Image> savedImages = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = s3Uploader.uploadFiles(image, "images");
            Image img = new Image(post, imageUrl);
            savedImages.add(imageRepository.save(img));
        }
        return savedImages;
    }

    @Transactional
    public Image updateImage(Long imageId, MultipartFile newImage) throws IOException {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        // 기존 이미지 삭제
        s3Uploader.deleteFile(image.getImageName());

        // 새 이미지 업로드
        String newImageUrl = s3Uploader.uploadFiles(newImage, "images");
        image.setImageName(newImageUrl);

        return imageRepository.save(image);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));

        // S3에서 이미지 삭제
        s3Uploader.deleteFile(image.getImageName());

        // db에서 이미지 삭제
        imageRepository.delete(image);
    }
}