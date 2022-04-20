package itmo.blps.delegates;

import itmo.blps.domain.Test;
import itmo.blps.domain.User;
import itmo.blps.dto.AttemptDTO;
import itmo.blps.repos.TestRepo;
import itmo.blps.repos.UserRepo;
import itmo.blps.service.VariablesService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Named
public class SendRequest implements JavaDelegate {
    private final RabbitTemplate rabbitTemplate;
    private final String topicExchangeName = "spring-boot";
    User user;
    @Autowired
    UserRepo userRepo;
    @Autowired
    TestRepo testRepo;
    @Autowired
    VariablesService variablesService;

    public SendRequest(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws IOException {
        user = variablesService.getUser();
        Test t = variablesService.getTest(delegateExecution);
        Integer quantity = (Integer) delegateExecution.getVariable("attemptCount");
        AttemptDTO attemptDTO = new AttemptDTO();
        attemptDTO.setTestId(t.getId());
        attemptDTO.setQuantity(quantity);
        attemptDTO.setUserId(user.getId());
        rabbitTemplate.convertAndSend(topicExchangeName, "attempts.key", convertObjectToBytes(attemptDTO));
    }
    public static byte[] convertObjectToBytes(Object obj) throws IOException {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ous = new ObjectOutputStream(boas)) {
            ous.writeObject(obj);
            return boas.toByteArray();
        }
    }
}