package itmo.blps.delegates;

import itmo.blps.domain.Test;
import itmo.blps.domain.User;
import itmo.blps.form.AnswerForm;
import itmo.blps.form.TestForm;
import itmo.blps.repos.*;
import itmo.blps.service.VariablesService;
import lombok.SneakyThrows;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;

@Named
public class UpdateProgress implements JavaDelegate {
    @Autowired
    TestRepo testRepo;
    @Autowired
    VariantRepo variantRepo;
    @Autowired
    CourseRepo courseRepo;
    @Autowired
    UserTestRepo userTestRepo;
    @Autowired
    UserCourseRepo userCourseRepo;
    @Autowired
    VariablesService variablesService;

    @SneakyThrows
    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        int numberOfRightAnswers = 0;
        User user = variablesService.getUser();
        TestForm testForm = variablesService.getTestForm(delegateExecution);
        Test t = variablesService.getTest(delegateExecution);

        Long numberOfQuestions = testRepo.findNumberOfQuestionsInTest(t.getId());
        ArrayList<AnswerForm> answers = testForm.getAnswers();
        //подсчет и внесение в БД проргресса теста и прогресса курса
        for (int i = 0; i < answers.size(); i++) {
            ArrayList<Long> rightAnswers = variantRepo.findAnswersByQuestion(answers.get(i).getQuestion_id());
            ArrayList<Long> userAnswers = answers.get(i).getVariant_id();
            Collections.sort(rightAnswers);
            Collections.sort(userAnswers);
            if (rightAnswers.equals(userAnswers)) {
                numberOfRightAnswers++;
            }
        }
        double progressOfTest = (100 * numberOfRightAnswers / (double) numberOfQuestions);
        double progressOfCourse = 0;
        ArrayList<Long> listOfTestsId = courseRepo.findAllTestsOfCourse(t.getWeek().getCourse().getId());
        for (int i = 0; i < listOfTestsId.size(); i++) {
            Long p = userTestRepo.getProgress(listOfTestsId.get(i), user.getId());
            if (p != null) {
                progressOfCourse = progressOfCourse + p;
            }
        }
        progressOfCourse = progressOfCourse / listOfTestsId.size();
        if (progressOfTest > t.getMinimum()) {
            userTestRepo.setStatus(t.getId(), user.getId());
        }
        userTestRepo.setProgress(t.getId(), user.getId(), (int) progressOfTest);
        userCourseRepo.setProgress(t.getWeek().getCourse().getId(), user.getId(), (int) progressOfCourse);
        //проверка пройденности курса (есть сертификат или нет) и если нет, установка true
        if (!userCourseRepo.checkCertificateStatus(t.getWeek().getCourse().getId(), user.getId())) {
            userCourseRepo.setCertificateStatus(t.getWeek().getCourse().getId(), user.getId());
        }
        delegateExecution.setVariable("testProgress", progressOfTest);
    }
}

