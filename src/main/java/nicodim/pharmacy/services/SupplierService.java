package main.java.nicodim.pharmacy.services;

import java.util.ArrayList;
import java.util.List;

import main.java.nicodim.pharmacy.dao.SuppliersDao;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.exceptions.ErrorCode;
import main.java.nicodim.pharmacy.exceptions.ValidationException;
import main.java.nicodim.pharmacy.models.Suppliers;
import main.java.nicodim.pharmacy.services.base.BaseService;

public class SupplierService extends BaseService {
	private static volatile SupplierService instance;
	private final SuppliersDao supplierDao;

	private SupplierService() {
		this.supplierDao = new SuppliersDao();
	}

	public static SupplierService getInstance() {
		if (instance == null) {
			synchronized (SupplierService.class) {
				if (instance == null) {
					instance = new SupplierService();
				}
			}
		}
		return instance;
	}

	public void registerSupplier(Suppliers supplier) throws BusinessException {
		validateSupplier(supplier);
		if (!supplierDao.registerSupplierQuery(supplier)) {
			throw new BusinessException("Error al registrar el proveedor en la base de datos",
					ErrorCode.DATABASE_ERROR);
		}
	}

	public void updateSupplier(Suppliers supplier) throws BusinessException {
		validateSupplier(supplier);
		if (!supplierDao.updateSupplierQuery(supplier)) {
			throw new BusinessException("Ha ocurrido un error al modificar los datos del proveedor",
					ErrorCode.DATABASE_ERROR);
		}
	}

	public void deleteSupplier(int id) throws BusinessException {
		if (!supplierDao.deleteSupplierQuery(id)) {
			throw new BusinessException("Ha ocurrido un error al eliminar al proveedor", ErrorCode.DATABASE_ERROR);
		}
	}

	@Override
	public List<?> listAllElements(String value) throws BusinessException {
		List<Suppliers> list = supplierDao.listSuppliersQuery(value);
		if (list == null) {
			throw new BusinessException("Ha ocurrido un error al listar proveedores", ErrorCode.DATABASE_ERROR);
		}
		return list;
	}

	private void validateSupplier(Suppliers supplier) throws ValidationException {
		List<String> errors = new ArrayList<>();
		if (supplier == null) {
			throw new ValidationException("El proveedor no puede ser nulo");
		}
		if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
			errors.add("El nombre del proveedor es requerido");
		}
		if (supplier.getEmail() != null && !isValidEmail(supplier.getEmail())) {
			errors.add("El formato del email no es válido");
		}
		if (!isValidPhone(supplier.getTelephone())) {
			errors.add("El formato del teléfono no es válido");
		}
		if (!errors.isEmpty()) {
			throw new ValidationException(String.join(", ", errors));
		}
	}

	private boolean isValidEmail(String email) {
		return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
	}

	private boolean isValidPhone(String phone) {
		return phone != null && phone.matches("^\\d{10}$");
	}
}