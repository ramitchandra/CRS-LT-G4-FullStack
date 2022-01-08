package com.lt.crs.controllers;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lt.bean.Course;
import com.lt.bean.Grades;
import com.lt.bean.Professor;
import com.lt.crs.business.CourseHandler;
import com.lt.crs.business.ProfessorHandler;
import com.lt.crs.business.StudentHandler;
import com.lt.crs.exception.CourseAlreadyExistException;
import com.lt.crs.exception.CourseIdNotFoundException;
import com.lt.crs.exception.CourseNotFoundException;
import com.lt.crs.exception.GradeNotFoundException;
import com.lt.crs.exception.InvalidStudentIdException;
import com.lt.crs.exception.NoPendingApprovalException;
import com.lt.crs.exception.ProfessorIdNotFoundException;
import com.lt.crs.exception.ProfessorNotFoundException;
import com.lt.crs.validation.UserAuthorization;
import com.lt.dao.AdminDao;
import com.lt.dao.GradesDAO;
import com.lt.dao.StudentDao;

@RestController
public class AdminController {
	
	@Autowired
	StudentHandler studentHandlerImpl;
	
	@Autowired
	AdminDao adminDaoImpl;
	
	@Autowired
	CourseHandler courseHandlerImpl;
	
	@Autowired
	ProfessorHandler professorHandlerImpl;
	
	@Autowired
	StudentDao studentDaoImpl;
	
	@Autowired
	UserAuthorization userAuthorization;
	
	@Autowired
	GradesDAO gradeDAOImpl;
	@RequestMapping(value = "/admin/listStudent", produces = MediaType.APPLICATION_JSON, method = RequestMethod.GET)
	public ResponseEntity<List<Map<String,String>>> adminListStudent() {
		userAuthorization.adminAuthorization();
		List<Map<String,String>> pendingApproval = studentDaoImpl.getStudents();
		if(pendingApproval.isEmpty())
			throw new NoPendingApprovalException();
		return new ResponseEntity<List<Map<String,String>>>(pendingApproval,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/validateStudent/{id}", produces = MediaType.APPLICATION_JSON, method = RequestMethod.PUT)
	public ResponseEntity<String> validateStudent(@PathVariable int id) {
		userAuthorization.adminAuthorization();
		if(adminDaoImpl.approveStudent(id)!=1)
			throw new InvalidStudentIdException();
		return new ResponseEntity<String>("Validated student: "+ id,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/addCourse", produces = MediaType.APPLICATION_JSON, method = RequestMethod.POST)
	public ResponseEntity<String> addCourse(@RequestBody Course course) {	
		userAuthorization.adminAuthorization();
		if(adminDaoImpl.addCourse(course)==-1)
			throw new CourseAlreadyExistException();
		return new ResponseEntity<String>("Course Added",HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/deleteCourse/{courseId}", produces = MediaType.APPLICATION_JSON, method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteCourse(@PathVariable int courseId) {		
		userAuthorization.adminAuthorization(); 
		if(adminDaoImpl.deleteCourse(courseId)!=1)
			 throw new CourseIdNotFoundException();
		 return new ResponseEntity<String>("Course Deleted: "+ courseId,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/getCourse", produces = MediaType.APPLICATION_JSON, method = RequestMethod.GET)
	public ResponseEntity<List<Course>> getCourse() {
		userAuthorization.adminAuthorization();
		List<Course> courseList=adminDaoImpl.getAllCourse();
		if(courseList.isEmpty())
			throw new CourseNotFoundException();
		return new ResponseEntity<List<Course>>(courseList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/addProfesor", produces = MediaType.APPLICATION_JSON, method = RequestMethod.POST)
	public ResponseEntity<String> addProfessor(@RequestBody Professor professor) {
		userAuthorization.adminAuthorization(); 
		adminDaoImpl.addProfessor(professor);
		return new ResponseEntity<String>("Professor Added",HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/getProfesor", produces = MediaType.APPLICATION_JSON, method = RequestMethod.GET)
	public ResponseEntity<List<Professor>> getProfessor() {
		userAuthorization.adminAuthorization();
		List<Professor> profList= adminDaoImpl.getProfessorList();
		if(profList.isEmpty())
			throw new ProfessorNotFoundException();
		return new ResponseEntity<List<Professor>>(profList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/deleteProfessor/{professorId}", produces = MediaType.APPLICATION_JSON, method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteProfessor(@PathVariable int professorId) {		
		userAuthorization.adminAuthorization(); 
		if(adminDaoImpl.deleteProfessor(professorId)!=1)
			 throw new ProfessorIdNotFoundException(); 
		 return new ResponseEntity<String>("Professor Deleted: " + professorId,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/generateReportCard", produces = MediaType.APPLICATION_JSON, method = RequestMethod.GET)
	public ResponseEntity<List<Grades>> generateReportCard() {
		userAuthorization.adminAuthorization();
		List<Grades> gradeLists = gradeDAOImpl.viewGrades();
		if(gradeLists.isEmpty())
			throw new GradeNotFoundException();
		return new ResponseEntity<List<Grades>>(gradeLists,HttpStatus.OK);
	}
}
