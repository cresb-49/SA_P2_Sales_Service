package com.sap.sales_service.snacks.application.usecases.updatesnack;

import com.sap.sales_service.snacks.application.input.UpdateSnackPort;
import com.sap.sales_service.snacks.application.ouput.DeletingFilePort;
import com.sap.sales_service.snacks.application.ouput.FindingSnackPort;
import com.sap.sales_service.snacks.application.ouput.SaveFilePort;
import com.sap.sales_service.snacks.application.ouput.SaveSnackPort;
import com.sap.sales_service.snacks.application.usecases.updatesnack.dtos.UpdateSnackDTO;
import com.sap.sales_service.snacks.domain.Snack;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UpdateSnackCase implements UpdateSnackPort {

    @Value("${bucket.name}")
    private String bucketName;

    @Value("${bucket.directory}")
    private String bucketDirectory;

    @Value("${aws.region}")
    private String awsRegion;

    private final FindingSnackPort findingSnackPort;
    private final SaveFilePort saveFilePort;
    private final DeletingFilePort deletingFilePort;
    private final SaveSnackPort saveSnackPort;

    @Override
    public Snack update(UpdateSnackDTO updateSnackDTO) {
        var now = String.valueOf(System.currentTimeMillis());
        var containsFile = updateSnackDTO.file() != null && !updateSnackDTO.file().isEmpty();
        var originalFileName = containsFile ? updateSnackDTO.file().getOriginalFilename() : null;
        var extension = containsFile ? getExtensionNoDotLower(originalFileName) : "";
        if (containsFile && !extension.matches("^(png|jpg|jpeg|gif)$")) {
            throw new RuntimeException("File must be png, jpg, jpeg or gif");
        }
        // Find existing snack
        var snack = findingSnackPort.findById(updateSnackDTO.id())
                .orElseThrow(() -> new RuntimeException("Snack with id " + updateSnackDTO.id() + " does not exist"));
        var oldUrl = snack.getImageUrl();
        // Update fields
        snack.update(
                updateSnackDTO.name(),
                updateSnackDTO.price(),
                containsFile ? calculateUrl(snack, extension, now) : oldUrl
        );
        //Validate snack
        snack.validate();
        //Save file if there is a new one and delete old one
        if (containsFile) {
            // Fist upload new file and if it works, delete old file
            saveFile(snack, updateSnackDTO.file(), extension, now);
            deleteFile(oldUrl);
        }
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

    private void saveFile(Snack snack, MultipartFile file, String extension, String now) {
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

    private void deleteFile(String url) {
        try {
            var urlParts = url.split("/");
            var keyName = urlParts[urlParts.length - 1];
            deletingFilePort.deleteFile(bucketName, bucketDirectory, keyName);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from bucket: " + e.getMessage());
        }
    }
}
