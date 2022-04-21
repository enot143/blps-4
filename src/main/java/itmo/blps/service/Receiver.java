package itmo.blps.service;

import itmo.blps.domain.Test;
import itmo.blps.domain.UserCourseKey;
import itmo.blps.domain.UserTest;
import itmo.blps.dto.AttemptDTO;
import itmo.blps.exceptions.TestException;
import itmo.blps.repos.CourseRepo;
import itmo.blps.repos.TestRepo;
import itmo.blps.repos.UserCourseRepo;
import itmo.blps.repos.UserTestRepo;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;


@Component
public class Receiver {
    String messageToUser;
    @Autowired
    TestRepo testRepo;
    @Autowired
    UserTestRepo userTestRepo;
    @Autowired
    UserCourseRepo userCourseRepo;
    @Autowired
    CourseRepo courseRepo;
    @Autowired
    RuntimeService runtimeService;

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    @Transactional
    public void receiveMessage(byte[] message) {
        try {
            AttemptDTO attemptDTO = (AttemptDTO) deserialize(message);
            if (testRepo == null) {
                System.out.println("error");
            }
            Test t = testRepo.findById(attemptDTO.getTestId()).orElseThrow(() -> new TestException("Test is not found"));
            //проверка что дедлайн теста не истек
            Date dateNow = new java.util.Date();
            if (t.getWeek().getDeadline().before(dateNow)) {
                throw new TestException("Missed deadline");
            }
            //проверка что пользователь уже выполнял тест и попытки закончились
            UserTest ut = userTestRepo.getUserTestByUserIdAndTestId(attemptDTO.getUserId(), attemptDTO.getTestId());
            if (ut == null) {
                throw new TestException("User has never started this test");
            }
            if (ut.getAttempts() > 0) {
                throw new TestException("User still have enough attempts");
            }
            //проверка чт лимит добавляемых попыток на курс не потрачен
            int availableAttempts = courseRepo.getCourseById(t.getWeek().getCourse().getId()).getMaxAttempts();
            UserCourseKey userCourseKey = new UserCourseKey();
            userCourseKey.setCourseId(t.getWeek().getCourse().getId());
            userCourseKey.setUserId(attemptDTO.getUserId());
            int addedAttempts = userCourseRepo.getUserCourseById(userCourseKey).getAdded_attempts();
            if (addedAttempts + attemptDTO.getQuantity() <= availableAttempts) {
                userTestRepo.addAttempts(t.getId(), attemptDTO.getUserId(), attemptDTO.getQuantity());
                userCourseRepo.setAttempts(t.getWeek().getCourse().getId(), attemptDTO.getUserId(), addedAttempts + attemptDTO.getQuantity());
                System.out.println("Successfully added attempts");
                messageToUser = "Successfully added attempts";
            } else throw new TestException("Limit is less than requested quantity");
        } catch (IOException | ClassNotFoundException | IllegalArgumentException | TestException e) {
            System.out.println("Fail : " + e.getMessage());
            messageToUser = e.getMessage();
//            e.printStackTrace();
        }
        runtimeService.createMessageCorrelation("AttemptMessage")
                .setVariableLocal("messageAttempts", messageToUser)
                .correlate();
    }
}


