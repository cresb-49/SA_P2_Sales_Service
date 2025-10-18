package com.sap.sales_service.snacks.application.usecases.createsnack;

import com.sap.common_lib.util.FileExtensionUtils;
import com.sap.sales_service.snacks.application.input.CreateSnackPort;
import com.sap.sales_service.snacks.application.ouput.FindingSnackPort;
import com.sap.sales_service.snacks.application.ouput.SaveFilePort;
import com.sap.sales_service.snacks.application.ouput.SaveSnackPort;
import com.sap.sales_service.snacks.application.usecases.createsnack.dtos.CreateSnackDTO;
import com.sap.sales_service.snacks.domain.Snack;
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
    private final FindingSnackPort findingSnackPort;
    private final SaveFilePort saveFilePort;

    @Override
    public Snack create(CreateSnackDTO createSnackDTO) {
        // Find snack by like name in the same cinema
        findingSnackPort.findLikeNameAndCinemaId(
                createSnackDTO.name(),
                createSnackDTO.cinemaId()
        ).ifPresent(s -> {
            throw new IllegalArgumentException("Snack with name " + createSnackDTO.name() + " already exists in the cinema");
        });
        var now = String.valueOf(System.currentTimeMillis());
        var hasExternalUrl = createSnackDTO.urlImage() != null && !createSnackDTO.urlImage().isBlank();
        if ((createSnackDTO.file() == null || createSnackDTO.file().isEmpty()) && !hasExternalUrl) {
            throw new IllegalArgumentException("File is required");
        }
        var originalFileName = createSnackDTO.file() != null ? createSnackDTO.file().getOriginalFilename() : "";
        var extension = FileExtensionUtils.getExtensionNoDotLower(originalFileName);
        if (!extension.matches("^(png|jpg|jpeg|gif)$") && !hasExternalUrl) {
            throw new IllegalArgumentException("File must be png, jpg, jpeg or gif");
        }
        // Process file and assign url
        var url = hasExternalUrl ? createSnackDTO.urlImage() : calculateUrl(extension, now);
        //Create domain object
        var snack = new Snack(
                createSnackDTO.cinemaId(),
                createSnackDTO.name(),
                createSnackDTO.price(),
                hasExternalUrl,
                url
        );
        //Validate snack
        snack.validate();
        //Save file
        if (!hasExternalUrl) {
            saveFile(createSnackDTO.file(), extension, now);
        }
        //Save snack
        return saveSnackPort.save(snack);
    }

    private String calculateUrl(String extension, String now) {
        var fileName = String.format("snack_%s.%s", now, extension);
        return String.format("https://%s.s3.%s.amazonaws.com/%s/%s", bucketName, awsRegion, bucketDirectory, fileName);
    }

    private void saveFile(MultipartFile file, String extension, String now) {
        try {
            saveFilePort.uploadFile(
                    bucketName,
                    bucketDirectory,
                    String.format("snack_%s.%s", now, extension),
                    file.getBytes()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error saving file", e);
        }
    }
}
