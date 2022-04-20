package itmo.blps.repos;


import itmo.blps.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface QuestionRepo extends JpaRepository<Question, Long> {
    Question findQuestionById(Long id);
    ArrayList<Question> findAllByTestId(Long id);
    void deleteAllByTestId(Long id);
}