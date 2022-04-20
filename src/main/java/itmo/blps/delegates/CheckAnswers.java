package itmo.blps.delegates;

import itmo.blps.domain.Test;
import itmo.blps.form.AnswerForm;
import itmo.blps.form.TestForm;
import itmo.blps.repos.TestRepo;
import itmo.blps.service.VariablesService;
import lombok.SneakyThrows;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.ArrayList;

@Named
public class CheckAnswers implements JavaDelegate {
    @Autowired
    TestRepo testRepo;
    @Autowired
    VariablesService variablesService;

    @SneakyThrows
    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        boolean answersBool = true;
        Test t = variablesService.getTest(delegateExecution);
        TestForm testForm = variablesService.getTestForm(delegateExecution);
        ArrayList<AnswerForm> answers = testForm.getAnswers();
        Long numberOfQuestions = testRepo.findNumberOfQuestionsInTest(t.getId());
        if (answers.size() != numberOfQuestions){
            answersBool = false;
        }
        delegateExecution.setVariableLocal("answersCount", answersBool);
    }
}