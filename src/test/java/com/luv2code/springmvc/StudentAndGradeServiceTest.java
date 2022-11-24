package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {
    @Autowired
    private StudentAndGradeService studentService;
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private MathGradesDao mathGradeDao;
    @Autowired
    private ScienceGradesDao scienceGradeDao;
    @Autowired
    private HistoryGradesDao historyGradeDao;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;


    @BeforeEach
    public void beforeEach() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }

    @Test
    public void createStudentService() {
        studentService.createStudent("Chad", "Darby", "chad.Darby@luv2code_scool.com");
        CollegeStudent student = studentDao.findByEmailAddress("chad.Darby@luv2code_scool.com");
        assertEquals("chad.Darby@luv2code_scool.com", student.getEmailAddress(), "find by email");

    }

    @Test
    public void createGradeService() {
//        create the grade
        assertTrue(studentService.createGrade(80.50, 1, "math"));
        assertTrue(studentService.createGrade(80.50, 1, "science"));
        assertTrue(studentService.createGrade(80.50, 1, "history"));
//        get all grades with studentId
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);
//        verify there is grades
//        assertTrue(mathGrades.iterator().hasNext(), "student has math grades");
//        assertTrue(scienceGrades.iterator().hasNext(), "student has science grades");
//        assertTrue(historyGrades.iterator().hasNext(), "student has history grades");
        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2);
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2);
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2);
    }

    @Test
    public void isStudentNullCheck() {
        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));

    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> deleteCollegeStudent = studentDao.findById(1);
        Optional<MathGrade> deleteMathGrade = mathGradeDao.findById(1);
        Optional<ScienceGrade> deleteScienceGrade = scienceGradeDao.findById(1);
        Optional<HistoryGrade> deleteHistoryGrade = historyGradeDao.findById(1);
        assertTrue(deleteCollegeStudent.isPresent(), "Return true");
        assertTrue(deleteMathGrade.isPresent(), "Return true");
        assertTrue(deleteScienceGrade.isPresent(), "Return true");
        assertTrue(deleteHistoryGrade.isPresent(), "Return true");
        studentService.deleteStudent(1);
        deleteCollegeStudent = studentDao.findById(1);
        deleteMathGrade = mathGradeDao.findById(1);
        deleteScienceGrade = scienceGradeDao.findById(1);
        deleteHistoryGrade = historyGradeDao.findById(1);
        assertFalse(deleteCollegeStudent.isPresent(), "Return false");
        assertFalse(deleteMathGrade.isPresent(), "Return false");
        assertFalse(deleteScienceGrade.isPresent(), "Return false");
        assertFalse(deleteHistoryGrade.isPresent(), "Return false");

    }

    @Sql("/insertData.sql")
    @Test
    public void getGradeBookService() {
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradeBook();
        List<CollegeStudent> collegeStudents = new ArrayList<>();
        for (CollegeStudent collegeStudent : iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }
        assertEquals(5, collegeStudents.size());
    }

    @Test
    public void createGradeServiceReturnFalse() {
        assertFalse(studentService.createGrade(105, 1, "math"));
        assertFalse(studentService.createGrade(-5, 1, "math"));
        assertFalse(studentService.createGrade(80.50, 2, "math"));
        assertFalse(studentService.createGrade(80.50, 2, "literature"));

    }

    @Test
    public void deleteGradeService() {
        assertEquals(1, studentService.deleteGrade(1, "math"),
                "Return student id after delete grade");
        assertEquals(1, studentService.deleteGrade(1, "science"),
                "Return student id after delete grade");
        assertEquals(1, studentService.deleteGrade(1, "history"),
                "Return student id after delete grade");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero() {
        assertEquals(0, studentService.deleteGrade(0, "science"), "No student should have 0 id");
        assertEquals(0, studentService.deleteGrade(1, "literature"), "No student should have a literature class ");
    }

    @Test
    public void studentInformation(){
        GradebookCollegeStudent gradebookCollegeStudent=studentService.studentInformation(1);
        assertNotNull(gradebookCollegeStudent);
        assertEquals(1,gradebookCollegeStudent.getId());
        assertEquals("Eric",gradebookCollegeStudent.getFirstname());
        assertEquals("Roby",gradebookCollegeStudent.getLastname());
        assertEquals("eric.roby@luv2code_school.com",gradebookCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size()==1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size()==1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size()==1);


    }
    @Test
    public void studentInformationServiceReturnNull(){
        GradebookCollegeStudent gradebookCollegeStudent=studentService.studentInformation(0);
        assertNull(gradebookCollegeStudent);
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);

    }

}
