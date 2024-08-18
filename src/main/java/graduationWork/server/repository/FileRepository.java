package graduationWork.server.repository;

import graduationWork.server.domain.UploadFile;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FileRepository {

    private final EntityManager em;

    public Long save(UploadFile file) {
        em.persist(file);
        return file.getId();
    }

    public Long findByUploadName(String uploadName) {
        UploadFile uploadName1 = (UploadFile) em.createQuery("select f from UploadFile f where f.uploadName = :uploadName")
                .setParameter("uploadName", uploadName)
                .getSingleResult();
        return uploadName1.getId();
    }

    public UploadFile findById(Long id) {
        return em.find(UploadFile.class, id);
    }
}
