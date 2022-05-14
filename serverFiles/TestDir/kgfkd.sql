START TRANSACTION;
UPDATE dept_emp SET dept_emp.to_date = SYSDATE() WHERE dept_emp.emp_no = 3 AND dept_emp.to_date = '9999-01-01';
UPDATE dept_manager SET dept_manager.to_date = SYSDATE() WHERE dept_manager.emp_no = 3 AND dept_manager.to_date = '9999-01-01';
UPDATE  salaries SET salaries.to_date = SYSDATE() WHERE salaries.emp_no = 3 AND salaries.to_date = '9999-01-01';
UPDATE titles SET titles.to_date = SYSDATE() WHERE titles.emp_no = 3 AND titles.to_date = '9999-01-01';
COMMIT;