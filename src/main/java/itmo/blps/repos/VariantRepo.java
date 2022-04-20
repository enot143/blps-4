package itmo.blps.repos;

import itmo.blps.domain.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface VariantRepo extends JpaRepository<Variant, Long> {
    ArrayList<Variant> findAllByQuestionId(Long id);
    void deleteAllByQuestionId(Long id);
    @Query("SELECT v.id FROM Variant v WHERE v.question.id = :question_id")
    ArrayList<Long> findVariantIdByQuestion(@Param("question_id") Long question_id);

    @Query("SELECT v.description FROM Variant v WHERE v.id = :variant_id")
    String findVariantDescriptionById(@Param("variant_id") Long variant_id);

    @Query("SELECT v.id FROM Variant v WHERE v.question.id = :question_id AND v.correct = TRUE")
    ArrayList<Long> findAnswersByQuestion(@Param("question_id") Long question_id);
}