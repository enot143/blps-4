package itmo.blps.delegates;

import itmo.blps.domain.Test;
import itmo.blps.domain.User;
import itmo.blps.domain.UserTest;
import itmo.blps.domain.UserTestKey;
import itmo.blps.repos.TestRepo;
import itmo.blps.repos.UserTestRepo;
import itmo.blps.service.VariablesService;
import lombok.SneakyThrows;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class CheckAttempts implements JavaDelegate {
    @Autowired
    TestRepo testRepo;
    @Autowired
    UserTestRepo userTestRepo;
    @Autowired
    VariablesService variablesService;


    @SneakyThrows
    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        boolean attempts = true;
        Test t = variablesService.getTest(delegateExecution);
        User user = variablesService.getUser();
        //если не существует еще записи о прохождении теста, создать ее
        if (userTestRepo.getUserTestByUser(user) == null){
            UserTest ut = new UserTest();
            UserTestKey userTestKey = new UserTestKey();
            ut.setId(userTestKey);
            ut.setTest(t);
            ut.setStatus(false);
            ut.setUser(user);
            ut.setAttempts(5);
            ut.setProgress(0);
            userTestRepo.save(ut);
        }

        if (userTestRepo.getAttempts(t.getId(), user.getId()) > 0) {
            userTestRepo.setAttempts(t.getId(), user.getId());
        } else {
            attempts = false;
        }
        delegateExecution.setVariableLocal("attempts", attempts);
    }
}
