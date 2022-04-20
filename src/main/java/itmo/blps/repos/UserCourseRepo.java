package itmo.blps.repos;


import itmo.blps.domain.UserCourse;
import itmo.blps.domain.UserCourseKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCourseRepo extends JpaRepository<UserCourse, UserCourseKey> {
    @Query("SELECT COUNT (*)  FROM UserCourse uc WHERE uc.certificate_status = TRUE")
    Long getCertificateCount();

    UserCourse getUserCourseByCourseIdAndUserId(Long courseId, Long userId);

    @Query("SELECT uc.user.id FROM UserCourse uc WHERE uc.course.id = :course_id AND uc.user.id = :user_id")
    Long checkUserCourse(@Param("course_id") Long course_id, @Param("user_id") Long user_id);

    @Modifying
    @Query("UPDATE UserCourse uc SET uc.progress = :progress WHERE uc.course.id = :course_id AND uc.user.id = :user_id")
    void setProgress(@Param("course_id") Long course_id, @Param("user_id") Long user_id, @Param("progress") Integer progress);

    @Query("SELECT uc.certificate_status FROM UserCourse uc WHERE uc.course.id = :course_id AND uc.user.id = :user_id")
    boolean checkCertificateStatus(@Param("course_id") Long course_id, @Param("user_id") Long user_id);

    @Modifying
    @Query("UPDATE UserCourse uc SET uc.certificate_status = true WHERE uc.course.id = :course_id AND uc.user.id = :user_id")
    void setCertificateStatus(@Param("course_id") Long course_id, @Param("user_id") Long user_id);

    @Modifying
    @Query("UPDATE UserCourse uc SET uc.added_attempts = :attempts WHERE uc.course.id = :course_id AND uc.user.id = :user_id")
    void setAttempts(@Param("course_id") Long course_id, @Param("user_id") Long user_id, @Param("attempts") Integer attempts);
}