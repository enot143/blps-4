package itmo.blps.delegates;

import itmo.blps.domain.Test;
import itmo.blps.domain.User;
import itmo.blps.dto.TestDTO;
import itmo.blps.exceptions.TestException;
import itmo.blps.repos.UserCourseRepo;
import itmo.blps.service.TestService;
import itmo.blps.service.VariablesService;
import lombok.SneakyThrows;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class GetTest implements JavaDelegate {
    @Autowired
    TestService testService;
    @Autowired
    IdentityService identityService;
    @Autowired
    VariablesService variablesService;
    @Autowired
    UserCourseRepo userCourseRepo;

    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        User u = variablesService.getUser();
        if (u != null) {
            Test test = variablesService.getTest(delegateExecution);
            if (test == null ) throw new BpmnError("Not_Found", "Test is not found");
            TestDTO testDTO = testService.get(test.getId());
            delegateExecution.setVariable("registration", checkRegister(test, u));
            delegateExecution.setVariableLocal("test", Variables.objectValue(testDTO).serializationDataFormat("application/json").create());
        } else delegateExecution.setVariableLocal("registration", false);
    }

    private boolean checkRegister(Test t, User user) {
        return (userCourseRepo.checkUserCourse(t.getWeek().getCourse().getId(), user.getId()) != null);
    }
}

