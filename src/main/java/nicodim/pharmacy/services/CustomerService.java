package main.java.nicodim.pharmacy.services;

import java.util.ArrayList;
import java.util.List;

import main.java.nicodim.pharmacy.dao.CustomersDao;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.exceptions.ErrorCode;
import main.java.nicodim.pharmacy.exceptions.ValidationException;
import main.java.nicodim.pharmacy.models.Customers;
import main.java.nicodim.pharmacy.services.base.BaseService;

public class CustomerService extends BaseService {
	private final CustomersDao customerDao;
	private static volatile CustomerService instance;

	private CustomerService() {
		this.customerDao = new CustomersDao();
	}

	public static CustomerService getInstance() {
		if (instance == null) {
			synchronized (CustomerService.class) {
				if (instance == null) {
					instance = new CustomerService();
				}
			}
		}
		return instance;
	}

	public void registerCustomer(Customers customer) throws BusinessException {
		validateCustomer(customer);
		// Verificar si ya existe un cliente con el mismo ID
		if (customerDao.exists(customer.getId())) {
			throw new BusinessException("Ya existe un cliente con el ID " + customer.getId(),
					ErrorCode.DUPLICATE_ENTITY);
		}
		if (!customerDao.registerCustomerQuery(customer)) {
			throw new BusinessException("Error al registrar el cliente en la base de datos", ErrorCode.DATABASE_ERROR);
		}
	}

	public void updateCustomer(Customers customer) throws BusinessException {
		validateCustomer(customer);
		if (!customerDao.updateCustomerQuery(customer)) {
			throw new BusinessException("Ha ocurrido un error al modificar los datos del cliente",
					ErrorCode.DATABASE_ERROR);
		}
	}

	public void deleteCustomer(int id) throws BusinessException {
		if (!customerDao.deleteCustomerQuery(id)) {
			throw new BusinessException("Ha ocurrido un error al eliminar al cliente", ErrorCode.DATABASE_ERROR);
		}
	}

	@Override
	public List<?> listAllElements(String text) throws BusinessException {
		List<Customers> list = customerDao.listCustomersQuery(text);
		if (list == null) {
			throw new BusinessException("Ha ocurrido un error al listar clientes", ErrorCode.DATABASE_ERROR);
		}
		return list;
	}

	private void validateCustomer(Customers customer) throws ValidationException {
		List<String> errors = new ArrayList<>();
		if (customer == null) {
			throw new ValidationException("El cliente no puede ser nulo");
		}
		if (customer.getFull_name() == null || customer.getFull_name().trim().isEmpty()) {
			errors.add("El nombre completo es requerido");
		}
		if (customer.getEmail() != null && !isValidEmail(customer.getEmail())) {
			errors.add("El formato del email no es válido");
		}
		if (!isValidPhone(customer.getTelephone())) {
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

}
