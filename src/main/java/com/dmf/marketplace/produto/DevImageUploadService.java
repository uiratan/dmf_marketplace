package com.dmf.marketplace.produto;

import com.dmf.marketplace.compartilhado.ImageUploadService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("dev")
public class DevImageUploadService implements ImageUploadService {

    @Override
    public List<String> uploadImages(List<String> imageUrls) {
        // Gera links fictícios baseados nas URLs recebidas
        return imageUrls.stream()
                .map(url -> "http://fake-bucket.localhost/" + System.currentTimeMillis() + "-" + url.substring(url.lastIndexOf("/") + 1))
                .collect(Collectors.toList());
    }
}