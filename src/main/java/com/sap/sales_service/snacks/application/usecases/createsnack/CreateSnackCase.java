package com.sap.sales_service.snacks.application.usecases.createsnack;

import com.sap.sales_service.snacks.application.input.CreateSnackPort;
import com.sap.sales_service.snacks.application.ouput.SaveFilePort;
import com.sap.sales_service.snacks.application.ouput.SaveSnackPort;
import com.sap.sales_service.snacks.application.usecases.createsnack.dtos.CreateSnackDTO;
import com.sap.sales_service.snacks.domain.Snack;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CreateSnackCase implements CreateSnackPort {

    @Value("${bucket.name}")
    private String bucketName;

    @Value("${bucket.directory}")
    private String bucketDirectory;

    @Value("${aws.region}")
    private String awsRegion;

    private final SaveSnackPort saveSnackPort;
    private final SaveFilePort saveFilePort;

    @Override
    public Snack create(CreateSnackDTO createSnackDTO) {
        var now = String.valueOf(System.currentTimeMillis());
        if(createSnackDTO.file() == null || createSnackDTO.file().isEmpty()) {
            throw new RuntimeException("File is required");
        }
        var originalFileName = createSnackDTO.file().getOriginalFilename();
        var extension = getExtensionNoDotLower(originalFileName);
        if(!extension.matches("^(png|jpg|jpeg|gif)$")) {
            throw new RuntimeException("File must be png, jpg, jpeg or gif");
        }
        //Create domain object
        var snack = Snack.builder()
                .name(createSnackDTO.name())
                .price(createSnackDTO.price())
                .build();
        // Process file and assign url
        var url = calculateUrl(snack, extension, now);
        snack = snack.toBuilder().imageUrl(url).build();
        //Validate snack
        snack.validate();
        //Save file
        saveFile(snack, createSnackDTO.file(), extension, now);
        //Save snack
        return saveSnackPort.save(snack);
    }

    private String getExtensionWithDot(String name) {
        if (name == null) return "";
        String trimmed = name.trim();
        int lastDot = trimmed.lastIndexOf('.');
        // No punto o el punto es el primer char (dotfile) -> sin extensión "convencional"
        if (lastDot <= 0 || lastDot == trimmed.length() - 1) return "";
        return trimmed.substring(lastDot); // incluye el punto
    }

    private String getExtensionNoDotLower(String name) {
        String withDot = getExtensionWithDot(name);
        return withDot.isEmpty() ? "" : withDot.substring(1).toLowerCase(); // sin punto y en minúsculas
    }

    private String calculateUrl(Snack snack, String extension, String now) {
        var fileName = String.format("snack_%s_%s.%s", snack.getId(), now, extension);
        return String.format("https://%s.s3.%s.amazonaws.com/%s/%s", bucketName, awsRegion, bucketDirectory, fileName);
    }

    private void saveFile(Snack snack, MultipartFile file, String extension, String now){
        try {
            saveFilePort.uploadFile(
                    bucketName,
                    bucketDirectory,
                    String.format("snack_%s_%s.%s", snack.getId(), now, extension),
                    file.getBytes()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error saving file", e);
        }
    }
}
