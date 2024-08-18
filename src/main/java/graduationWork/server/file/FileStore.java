package graduationWork.server.file;

import graduationWork.server.domain.UploadFile;
import graduationWork.server.service.FileService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileStore {

    private final ServletContext servletContext;

    private String fileDir;

    @PostConstruct
    public void init() {
        // ServletContext를 사용하여 애플리케이션의 실제 루트 경로 가져오기
        String realPath = servletContext.getRealPath("/");
        this.fileDir = realPath + "files/";
        log.info("File directory set to: " + fileDir);
    }

    public String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {

        log.info("Current directory: " + System.getProperty("user.dir"));

        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            log.info("Storing file: " + multipartFile.getOriginalFilename());
            if(!multipartFile.isEmpty()) {
                log.info("File is not empty: " + multipartFile.getOriginalFilename());
                try {
                    storeFileResult.add(storeFile(multipartFile));
                } catch (IOException e) {
                    log.error("Failed to store file: " + multipartFile.getOriginalFilename(), e);
                }
            } else {
                log.info("File is empty: " + multipartFile.getOriginalFilename());
            }
        }
        for (UploadFile uploadFile : storeFileResult) {
            log.info("Upload File: " + uploadFile.getUploadFileName());
        }
        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()) {
            log.info("File is empty: " + multipartFile.getOriginalFilename());
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        log.info("Original Filename: " + originalFilename);
        log.info("Store Filename: " + storeFileName);

        // 파일을 저장할 디렉토리가 존재하지 않으면 생성
        File file = new File(getFullPath(storeFileName));
        File dir = file.getParentFile();
        if (!dir.exists()) {
            log.info("Directory does not exist, creating directory: " + dir.getAbsolutePath());
            dir.mkdirs();
        } else {
            log.info("Directory exists: " + dir.getAbsolutePath());
        }

        log.info("Saving file to: " + getFullPath(storeFileName));
        try {
            multipartFile.transferTo(file);
            log.info("File saved: " + getFullPath(storeFileName));
        } catch (IOException e) {
            log.error("Error saving file: " + getFullPath(storeFileName), e);
            throw e;
        }

        return new UploadFile(originalFilename, storeFileName);
    }

    public String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    public String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
