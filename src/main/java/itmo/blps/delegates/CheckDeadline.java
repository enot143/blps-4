package itmo.blps.delegates;

import itmo.blps.domain.Test;
import itmo.blps.service.VariablesService;
import lombok.SneakyThrows;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Date;

@Named
public class CheckDeadline implements JavaDelegate {
    @Autowired
    VariablesService variablesService;

    @SneakyThrows
    @Override
    @Transactional
    public void execute(DelegateExecution delegateExecution) {
        boolean deadline = true;
        Test t = variablesService.getTest(delegateExecution);
        Date dateNow = new java.util.Date();
        if (t.getWeek().getDeadline().before(dateNow)) {
            deadline = false;
        }
        delegateExecution.setVariableLocal("deadline", deadline);
    }
}

