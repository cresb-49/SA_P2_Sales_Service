package com.sap.sales_service.snacks.application.usecases.updatesnack;

import com.sap.common_lib.exception.NotFoundException;
import com.sap.common_lib.util.FileExtensionUtils;
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
        var hasExternalUrl = updateSnackDTO.urlImage() != null && !updateSnackDTO.urlImage().isBlank();
        var originalFileName = containsFile ? updateSnackDTO.file().getOriginalFilename() : null;
        var extension = containsFile ? FileExtensionUtils.getExtensionNoDotLower(originalFileName) : "";
        if (containsFile && !hasExternalUrl && !extension.matches("^(png|jpg|jpeg|gif)$")) {
            throw new IllegalArgumentException("File must be png, jpg, jpeg or gif");
        }
        var snack = findingSnackPort.findById(updateSnackDTO.id())
                .orElseThrow(() -> new NotFoundException("Snack with id " + updateSnackDTO.id() + " does not exist"));
        var oldUrl = snack.getImageUrl();
        var oldIsExternal = snack.isExternalImage();
        // Decidir NUEVA URL (si hay URL externa, gana; si no, si hay file, usa S3; si no, se queda igual)
        var newUrl = hasExternalUrl
                ? updateSnackDTO.urlImage()
                : (containsFile ? calculateUrl(extension, now) : oldUrl);
        var urlChanged = !newUrl.equals(oldUrl);
        // Actualizar campos
        snack.update(
                updateSnackDTO.name(),
                updateSnackDTO.price(),
                urlChanged ? hasExternalUrl : oldIsExternal, // si cambió y viene externa => true
                urlChanged ? newUrl : oldUrl
        );
        // Validaciones de dominio
        snack.validate();
        // Si se usará archivo (no externo) y la URL cambió => SUBIR primero
        if (!hasExternalUrl && containsFile && urlChanged) {
            saveFile(snack, updateSnackDTO.file(), extension, now);
        }
        var saved = saveSnackPort.save(snack);
        // Si cambió la URL y el anterior era local, BORRAR archivo viejo
        if (urlChanged && !oldIsExternal && oldUrl != null && !oldUrl.isBlank()) {
            deleteFile(oldUrl);
        }
        return saved;
    }

    private String calculateUrl(String extension, String now) {
        var fileName = String.format("snack_%s.%s", now, extension);
        return String.format("https://%s.s3.%s.amazonaws.com/%s/%s", bucketName, awsRegion, bucketDirectory, fileName);
    }

    private void saveFile(Snack snack, MultipartFile file, String extension, String now) {
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
