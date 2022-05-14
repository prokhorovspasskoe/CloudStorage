CREATE VIEW depart_view AS
SELECT departments.dept_no, departments.dept_name, employees.first_name, employees.last_name, COUNT(employees.emp_no) AS count_employees FROM departments 
LEFT JOIN dept_manager ON departments.dept_no = dept_manager.dept_no AND dept_manager.to_date = '9999-01-01'
LEFT JOIN dept_emp ON dept_emp.dept_no = departments.dept_no
LEFT JOIN employees ON dept_manager.emp_no = employees.emp_no 
GROUP BY departments.dept_no;

SET GLOBAL log_bin_trust_function_creators = 1;
DELIMITER $$
CREATE FUNCTION salaries_func (n_d CHAR(4)) RETURNS INT
BEGIN
DECLARE avg_sel INT;
SET avg_sel = (SELECT avg(salaries.salary) FROM salaries,  dept_emp
 WHERE dept_emp.emp_no = salaries.emp_no AND dept_emp.dept_no = n_d AND salaries.to_date = '9999-01-01');
RETURN avg_sel;
END$$
DELIMITER ;

SELECT salaries_func('d002') as salary_dep;

SELECT emp_no, hire_date, birth_date FROM employees;

DELIMITER $$
CREATE PROCEDURE `p_delete_employees` (IN emp_no_del INT)
BEGIN
UPDATE dept_emp SET dept_emp.to_date = GETDATE() WHERE dept_emp.emp_no = emp_no_del AND dept_emp.to_date = '9999-01-01';
UPDATE dept_manager SET dept_manager.to_date = GETDATE() WHERE dept_manager.emp_no = emp_no_del AND dept_manager.to_date = '9999-01-01';
UPDATE  salaries SET salaries.to_date = GETDATE() WHERE salaries.emp_no = emp_no_del AND salaries.to_date = '9999-01-01';
UPDATE titles SET titles.to_date = GETDATE() WHERE titles.emp_no = emp_no_del AND titles.to_date = '9999-01-01';
END
DELIMITER ;

DELIMITER $$
CREATE DEFINER = CURRENT_USER TRIGGER `employees`.`add_employees_BEFORE_INSERT` BEFORE INSERT ON `employees` FOR EACH ROW
DECLARE add_email_for_emp CHAR(50);
SET add_email_for_emp = CONCAT((SELECT first_name FROM employees),(SELECT last_name FROM employees),'@ourcompany.com');
INSERT INTO employees (e_mail) values(NEW.add_email_for_emp);
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE `p_add_new_employee` (IN p_first_name VARCHAR(14),
									   IN p_last_name VARCHAR(16),
                                       IN p_gender ENUM('M','F'),
                                       IN p_birth_date DATE,
                                       IN p_dept_no CHAR(4),
                                       IN p_title VARCHAR(50),
                                       OUT po_emp_no INT
                                       )
BEGIN
	DECLARE d_from_date DATE DEFAULT CURRENT_DATE;
    DECLARE d_to_date DATE DEFAULT date('9999-01-01');

	ALTER TABLE employees ADD COLUMN e_mail CHAR(50);
	select max(emp_no)+1 into po_emp_no from employees;

	INSERT INTO employees (emp_no, hire_date, first_name, last_name, gender, birth_date) 
	VALUES (po_emp_no, d_from_date, p_first_name, p_last_name, p_gender, p_birth_date);

	INSERT INTO dept_emp VALUES (po_emp_no, p_dept_no, d_from_date, d_to_date);

	INSERT INTO titles VALUES (po_emp_no, p_title, d_from_date, d_to_date);
END$$

DELIMITER ;