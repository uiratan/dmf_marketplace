package com.dmf.marketplace.compartilhado;

import java.io.IOException;
import java.util.List;

public interface ImageUploadService {
    List<String> uploadImages(List<String> imageUrls) throws IOException;
}
