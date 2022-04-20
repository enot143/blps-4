package itmo.blps.delegates;

import itmo.blps.domain.Stat;
import itmo.blps.domain.UserCourse;
import itmo.blps.repos.CourseRepo;
import itmo.blps.repos.StatRepo;
import itmo.blps.repos.UserCourseRepo;
import itmo.blps.repos.UserRepo;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;

@Named
@Component
public class CalculateStatistics implements JavaDelegate {
    @Autowired
    UserRepo userRepo;
    @Autowired
    CourseRepo courseRepo;
    @Autowired
    UserCourseRepo userCourseRepo;
    @Autowired
    StatRepo statRepo;

    @Transactional
    public void execute(DelegateExecution delegateExecution)  {
        Date dateNow = new java.util.Date();
        long users = userRepo.count();
        long courses = courseRepo.count();
        long money = 0;

        ArrayList<UserCourse> userCourses = (ArrayList<UserCourse>) userCourseRepo.findAll();
        for (UserCourse uc : userCourses){
            money += courseRepo.findCourseById(uc.getCourse().getId()).getCost();
        }

        long certificates = userCourseRepo.getCertificateCount();

        Stat stat = new Stat();
        stat.setCertificates(certificates);
        stat.setCourses(courses);
        stat.setDate(dateNow);
        stat.setMoney(money);
        stat.setUsers(users);
        statRepo.save(stat);

        System.out.println("statistic");
        System.out.println("date: " + dateNow);
        System.out.println("users: " + users);
        System.out.println("money: " + money);
        System.out.println("courses: " + courses);
        System.out.println("certificates: " + certificates);
    }
}
