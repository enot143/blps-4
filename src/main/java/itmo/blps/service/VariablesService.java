package itmo.blps.service;

import itmo.blps.domain.Test;
import itmo.blps.domain.User;
import itmo.blps.form.TestForm;
import itmo.blps.repos.TestRepo;
import itmo.blps.repos.UserRepo;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.camunda.spin.DataFormats.json;
import static org.camunda.spin.Spin.JSON;
import static org.camunda.spin.Spin.S;

@Service
public class VariablesService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    IdentityService identityService;
    @Autowired
    TestRepo testRepo;

    public VariablesService(UserRepo userRepo, IdentityService identityService, TestRepo testRepo) {
        this.userRepo = userRepo;
        this.identityService = identityService;
        this.testRepo = testRepo;
    }

    public User getUser() {
        String userId = identityService.getCurrentAuthentication().getUserId();
        org.camunda.bpm.engine.identity.User user = identityService.createUserQuery().userId(userId).singleResult();
        String email = user.getEmail();
        return userRepo.findUserByEmail(email);
    }

    public Test getTest(DelegateExecution delegateExecution) {
        Integer id = (Integer) delegateExecution.getVariable("testId");
        Long idL = Long.valueOf(id);
        return testRepo.findTestById(idL);
    }

    public TestForm getTestForm(DelegateExecution delegateExecution) {
        String testFormString = (String) delegateExecution.getVariable("answer");
        SpinJsonNode json = S(testFormString, json());
        return JSON(json).mapTo(TestForm.class);
    }

}
