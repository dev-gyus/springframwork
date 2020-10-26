package com.coderby.myapp.employees.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.coderby.myapp.employees.valueobject.EmpVO;

@Repository
public class EmpRepository implements IempRepository{
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private class EmpMapper implements RowMapper<EmpVO>{
		@Override
		public EmpVO mapRow(ResultSet rs, int count) throws SQLException {
			EmpVO emp = new EmpVO();
			emp.setEmployeeId(rs.getInt("employee_id"));
			emp.setFirstName(rs.getString("first_name"));
			emp.setLastName(rs.getString("last_name"));
			emp.setEmail(rs.getString("email"));
			emp.setPhoneNumber(rs.getString("phone_number"));
			emp.setHireDate(rs.getDate("hire_date"));
			
			emp.setJobId(rs.getString("job_id"));
			emp.setSalary(rs.getDouble("salary"));
			emp.setCommissionPct(rs.getDouble("commission_pct"));
			emp.setManagerId(rs.getInt("manager_id"));
			emp.setDepartmentId(rs.getInt("department_id"));
			return emp;
		}
	}
	@Override
	public int getEmpCount() {
		String sql = "select count(*) from employees";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}
	
	@Override
	public int getEmpCount(int deptid) {
		String sql = "select count(*) from employees where department_id=?";
		return jdbcTemplate.queryForObject(sql, Integer.class, deptid);
	}
	
	@Override
	public List<EmpVO> getEmpList() {
		String sql = "select * from employees";
		return jdbcTemplate.query(sql, new EmpMapper());
	}
	
	@Override
	public EmpVO getEmpInfo(int empid) {
		String sql = "select employee_id, first_name, last_name, email, phone_number, "
				+"hire_date, job_id, salary, commission_pct, manager_id, department_id "
				+"from employees where employee_id=?";
		return jdbcTemplate.queryForObject(sql, new EmpMapper(), empid);
	}
	
	@Override
	public void updateEmp(EmpVO emp) {
		String sql = "update employees set first_name=?, last_name=?, email=?, phone_number=?, hire_date=?, job_id=?, salary=?, commission_pct=?, manager_id=?, department_id=? where employee_id=?";
		
		/*에러발생이유 = DB에 입력하는 입사날짜(hire_Date)의 형식이 페이지 조회시 yyyy-mm-dd형으로 브라우저에 출력되는데,
						 이를 yyyy-mm-dd형으로 입력하면 날짜 입력 형식이 다르게 입력됨.
						 따라서 yyyy-mm-dd형을 dd-mon-yyyy형(ex: 20-MAR-2020)으로 내용을 바꿔야함*/
		
		/*
		Date from = emp.getHireDate();
		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
		String to = fm.format(from);
		System.out.println("to값 = " + to);
		
		StringBuilder as = new StringBuilder();
		char monthChar[] = null;
		for(int j=0; j<to.length(); j++) {
			as.append(to.charAt(j));
		}
		System.out.println("as값 = " + as);
		
		String month[] = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
		Map<String, Object> map = new HashMap<String, Object>();
		for(int j=0; j<12; j++) {
			map.put("0" + Integer.toString(j+1), month[j]);
		}
		as.getChars(5, 7, monthChar, 0);
		String monthKey = Character.toString(monthChar[0]) + Character.toString(monthChar[1]);
		
		System.out.println(monthChar);
		
		as.replace(5, 7, map.get(monthKey).toString());
		to = as.toString();
		try {
			from = fm.parse(to);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
		jdbcTemplate.update(sql, emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getPhoneNumber(), emp.getHireDate(),
				emp.getJobId(), emp.getSalary(), emp.getCommissionPct(), emp.getManagerId(), emp.getDepartmentId(), emp.getEmployeeId());
	}
	
	@Override
	public void insertEmp(EmpVO emp) {
		String sql = "insert into employees (employee_id, "
				+"first_name, last_name, email, phone_number, hire_date, "
				+"job_id, salary, commission_pct, manager_id, department_id) "
				+"values (?,?,?,?,?,sysdate,?,?,?,?,?)";
		jdbcTemplate.update(sql, emp.getEmployeeId(), emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getPhoneNumber(),
				emp.getJobId(), emp.getSalary(), emp.getCommissionPct(), emp.getManagerId(), emp.getDepartmentId());
	}
	
	@Override
	public void deleteJobHistory(int empid) {
		String sql = "delete from job_history where employee_id=?";
		jdbcTemplate.update(sql, empid);
	}
	
	@Override
	public void deleteEmp(int empid, String email) {
		String sql = "delete from employees where employee_id=? and email=?";
		jdbcTemplate.update(sql, empid, email);
	}
	
	@Override
	public List<Map<String, Object>> getAllDeptId() {
		String sql = "select department_id as departmentId, "
				+"		department_name as departmentName"
				+" from departments";
		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getAllJobId(){
		String sql = "select job_id as jobId, job_title as title from jobs";
		return jdbcTemplate.queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getAllManagerId(){
		String sql = "select "
				+"d.manager_id as managerId, e.first_name as firstName "
				+"from departments d join employees e "
				+"on d.manager_id = employee_id "
				+"order by d.manager_id";
		return jdbcTemplate.queryForList(sql);
	}

}
