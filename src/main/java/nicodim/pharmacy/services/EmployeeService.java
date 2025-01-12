package main.java.nicodim.pharmacy.services;

import java.util.ArrayList;
import java.util.List;

import main.java.nicodim.pharmacy.controllers.LoginController;
import main.java.nicodim.pharmacy.dao.EmployeesDao;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.exceptions.ErrorCode;
import main.java.nicodim.pharmacy.exceptions.ValidationException;
import main.java.nicodim.pharmacy.models.Employees;
import main.java.nicodim.pharmacy.services.base.BaseService;

public class EmployeeService extends BaseService {

	private EmployeesDao employeeDao;
	private static volatile EmployeeService instance;

	private EmployeeService() {
		this.employeeDao = new EmployeesDao();
	}

	public static EmployeeService getInstance() {
		if (instance == null) {
			synchronized (EmployeeService.class) {
				if (instance == null) {
					instance = new EmployeeService();
				}
			}
		}
		return instance;
	}

	public void registerEmployee(Employees employee) throws BusinessException {
		validateEmployee(employee);
		if (employeeDao.exists(employee.getId())) {
			throw new BusinessException("Ya existe un empleado con el ID " + employee.getId(),
					ErrorCode.DUPLICATE_ENTITY);
		}
		if (!employeeDao.registerEmployeeQuery(employee)) {
			throw new BusinessException("Error al registrar el empleado en la base de datos", ErrorCode.DATABASE_ERROR);
		}
	}

	public void updateEmployee(Employees employee) throws BusinessException {
		validateEmployee(employee);
		if (!employeeDao.updateEmployeeQuery(employee)) {
			throw new BusinessException("Ha ocurrido un error al modificar los datos del empleado.",
					ErrorCode.DATABASE_ERROR);
		}
	}

	public void deleteEmployee(int id) throws BusinessException {
		if (!employeeDao.deleteEmployeeQuery(id)) {
			throw new BusinessException("Ha ocurrido un error al eliminar al empleado", ErrorCode.DATABASE_ERROR);
		}
	}

	@Override
	public List<?> listAllElements(String text) throws BusinessException {
		List<Employees> list = employeeDao.listEmployeesQuery(text);
		if (list == null) {
			throw new BusinessException("Ha ocurrido un error al listar empleados.", ErrorCode.DATABASE_ERROR);
		}
		return list;
	}

	private void validateEmployee(Employees employee) throws ValidationException {
		List<String> errors = new ArrayList<>();
		if (employee == null) {
			throw new ValidationException("El empleado no puede ser nulo");
		}
		if (employee.getFull_name() == null || employee.getFull_name().trim().isEmpty()) {
			errors.add("El nombre completo es requerido");
		}
		if (employee.getEmail() != null && !isValidEmail(employee.getEmail())) {
			errors.add("El formato del email no es válido");
		}
		if (!isValidPhone(employee.getTelephone())) {
			errors.add("El formato del teléfono no es válido");
		}
		if (!errors.isEmpty()) {
			throw new ValidationException(String.join(", ", errors));
		}

	}

	private boolean isValidEmail(String email) {
		return email != null && email.contains("@");
	}

	private boolean isValidPhone(String phone) {
		return phone != null && phone.matches("\\d+");
	}

	public void validateAndChangeSelfPassword(String password, String confirm_password) throws BusinessException {
		Employees employee = LoginController.employee;
		if (employee == null) {
			throw new ValidationException("El empleado no puede ser nulo");
		}
		if (password.equals("") || confirm_password.equals("")) {
			throw new ValidationException("Debe completar ambas contraseñas.");
		}
		if (!password.equals(confirm_password)) {
			throw new ValidationException("Las contraseñas deben coincidir.");
		}
		employee.setPassword(password);
		if (!employeeDao.updateEmployeePasswordQuery(employee)) {
			throw new BusinessException("Ha ocurrido un error modificar la contraseña.", ErrorCode.DATABASE_ERROR);
		}
		LoginController.employee.setPassword(confirm_password);
	}

	public int checkAndGetId(String text) throws ValidationException {
		try {
			Integer id = Integer.parseInt(text);
			if (id <= 0) {
				throw new ValidationException("EL Id debe ser mayor a cero.");
			}
			return id;
		} catch (NumberFormatException e) {
			throw new ValidationException("El Id no es un número válido.");
		}
	}

}
