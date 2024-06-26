package uz.mediasolutions.mdeliveryservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.mdeliveryservice.entity.Variation;

import java.util.List;

public interface VariationRepository extends JpaRepository<Variation, Long> {

    Page<Variation> findAllByOrderByNumberAsc(Pageable pageable);

    Page<Variation> findAllByProductNameRuContainsIgnoreCaseOrProductNameUzContainsIgnoreCaseOrMeasureUnitNameRuContainsIgnoreCaseOrMeasureUnitNameUzContainsIgnoreCaseOrderByNumberAsc(String pNameUz, String pNameRu, String mNameUz, String mNameRu, Pageable pageable);

    boolean existsByNumber(Long number);

    boolean existsByNumberAndId(Long number, Long id);

    boolean existsByNameUzOrNameRu(String nameUz, String nameRu);

    List<Variation> findAllByProductIdOrderByNumberAsc(Long productId);

}
