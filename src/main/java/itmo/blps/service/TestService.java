package itmo.blps.service;

import itmo.blps.domain.Test;
import itmo.blps.dto.QuestionDTO;
import itmo.blps.dto.TestDTO;
import itmo.blps.dto.VariantDTO;
import itmo.blps.exceptions.TestException;
import itmo.blps.repos.QuestionRepo;
import itmo.blps.repos.TestRepo;
import itmo.blps.repos.VariantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TestService {
    @Autowired
    TestRepo testRepo;
    @Autowired
    QuestionRepo questionRepo;
    @Autowired
    VariantRepo variantRepo;

    public TestDTO get(Long test) throws TestException {
        Test t = testRepo.findById(test).orElseThrow(() -> new TestException("Test is not found", HttpStatus.NOT_FOUND));
//        checkRegister(t);
        TestDTO testDTO = new TestDTO();
        ArrayList<QuestionDTO> questions = new ArrayList<>();
        questionRepo.findAllByTestId(t.getId()).forEach(q -> {
            QuestionDTO dto = new QuestionDTO();
            dto.setId(q.getId());
            dto.setDescription(q.getDescription());
            ArrayList<VariantDTO> variants = new ArrayList<>();
            ArrayList<Long> ids = variantRepo.findVariantIdByQuestion(q.getId());
            for (int i = 0; i < ids.size(); i++){
                VariantDTO v = new VariantDTO();
                v.setId(ids.get(i));
                v.setDescription(variantRepo.findVariantDescriptionById(ids.get(i)));
                variants.add(v);
            }
            dto.setListOfAnswers(variants);
            questions.add(dto);
        });
        testDTO.setTest(t);
        testDTO.setListOfQuestions(questions);
        return testDTO;
    }

}
