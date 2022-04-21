package itmo.blps.delegates;

import itmo.blps.domain.Question;
import itmo.blps.domain.Test;
import itmo.blps.domain.Variant;
import itmo.blps.domain.Week;
import itmo.blps.form.AddTestForm;
import itmo.blps.form.QuestionForm;
import itmo.blps.form.VariantForm;
import itmo.blps.repos.QuestionRepo;
import itmo.blps.repos.TestRepo;
import itmo.blps.repos.VariantRepo;
import itmo.blps.repos.WeekRepo;
import itmo.blps.service.VariablesService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.ArrayList;

@Named
public class AddNewTest implements JavaDelegate {
    @Autowired
    WeekRepo weekRepo;
    @Autowired
    VariablesService variablesService;
    @Autowired
    TestRepo testRepo;
    @Autowired
    QuestionRepo questionRepo;
    @Autowired
    VariantRepo variantRepo;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        AddTestForm testForm = variablesService.getAddTestForm(delegateExecution);
        Week w = variablesService.getWeek(delegateExecution);
        Week week = weekRepo.findWeekById(w.getId());
        if (week == null){
            throw new BpmnError("Add_Test_Error", "Week is not found");
        }
        Test t = new Test();
        t.setMinimum(testForm.getMinimum());
        t.setWeek(week);
        testRepo.save(t);
        addQuestions(t, testForm);
    }

    @Transactional
    void addQuestions(Test t, AddTestForm testForm) throws Exception {
        ArrayList<QuestionForm> questions = testForm.getQuestions();
        if (questions.size() == 0) throw new BpmnError("Add_Test_Error", "Test must have more than 0 questions");
        for (QuestionForm question : questions){
            Question q = new Question();
            q.setTest(t);
            q.setDescription(question.getDescription());
            questionRepo.save(q);
            ArrayList<VariantForm> listOfVariants = question.getListOfVariants();
            if (listOfVariants.size() == 0) throw new BpmnError("Add_Test_Error", "Each question must have more at least 1 correct answer");
            for (VariantForm variant : listOfVariants){
                Variant v = new Variant();
                v.setDescription(variant.getDescription());
                v.setCorrect(variant.isCorrect());
                v.setQuestion(q);
                variantRepo.save(v);
            }
        }
    }
}
