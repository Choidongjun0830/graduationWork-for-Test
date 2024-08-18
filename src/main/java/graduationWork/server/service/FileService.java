package graduationWork.server.service;

import graduationWork.server.domain.UploadFile;
import graduationWork.server.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    public void save(UploadFile file) {
        fileRepository.save(file);
    }

    public UploadFile findById(Long id) {
        return fileRepository.findById(id);
    }
}
