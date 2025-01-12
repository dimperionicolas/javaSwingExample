package main.java.nicodim.pharmacy.services;

import java.util.ArrayList;
import java.util.List;

import main.java.nicodim.pharmacy.controllers.LoginController;
import main.java.nicodim.pharmacy.dao.SalesDao;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.exceptions.ErrorCode;
import main.java.nicodim.pharmacy.exceptions.ValidationException;
import main.java.nicodim.pharmacy.models.Sales;
import main.java.nicodim.pharmacy.services.base.BaseService;

public class SaleService extends BaseService {
	private final SalesDao saleDao;
	private static volatile SaleService instance;

	private SaleService() {
		this.saleDao = new SalesDao();
	}

	public static SaleService getInstance() {
		if (instance == null) {
			synchronized (SaleService.class) {
				if (instance == null) {
					instance = new SaleService();
				}
			}
		}
		return instance;
	}

	public void registerSale(Sales sale) throws BusinessException {
		validateSale(sale);
		if (saleDao.exists(sale.getId())) {
			throw new BusinessException("Ya existe una venta con el ID " + sale.getId(), ErrorCode.DUPLICATE_ENTITY);
		}
		int employeeId = LoginController.employee.getId(); // Podria venir directamente en la venta
		if (!saleDao.registerSaleQuery(sale.getCustomer_id(), employeeId, sale.getTotal_to_pay())) {
			throw new BusinessException("Error al registrar la venta en la base de datos", ErrorCode.DATABASE_ERROR);
		}
	}

	@Override
	public List<?> listAllElements(String text) throws BusinessException {
		List<Sales> list = saleDao.listAllSalesQuery();
		if (list == null) {
			throw new BusinessException("Ha ocurrido un error al listar ventas", ErrorCode.DATABASE_ERROR);
		}
		return list;
	}

	private void validateSale(Sales sale) throws ValidationException {
		List<String> errors = new ArrayList<>();
		if (sale == null) {
			throw new ValidationException("La venta no puede ser nula");
		}
		if (sale.getTotal_to_pay() == 0 || sale.getCustomer_id() == 0) {
			errors.add("Faltan datos para registrar la venta.");
		} // TODO verificar formato o ya fue validad?

		if (!errors.isEmpty()) {
			throw new ValidationException(String.join(", ", errors));
		}

	}

	private boolean isValidEmail(String email) {
		// TODO Implementar validación de email. Podria ir en el baseService
		return email != null && email.contains("@");
	}

	private boolean isValidPhone(String phone) {
		// TODO Implementar validación de teléfono. Podria ir en el baseService
		return phone != null && phone.matches("\\d+");
	}

	public int getLastSaleId() throws ValidationException {
		int sale_id = saleDao.saleId();
		if (sale_id <= 0) {
			throw new ValidationException("EL Id debe ser mayor a cero.");
		}
		return sale_id;
	}

	public void registerSaleDetailQuery(int product_id, int saleId, int sale_quantity, double sale_price,
			double sale_subtotal) throws BusinessException {
		if (!saleDao.registerSaleDetailQuery(product_id, saleId, sale_quantity, sale_price, sale_subtotal)) {
			throw new BusinessException("Ha ocurrido un error al registrar los detalles de la venta.",
					ErrorCode.DATABASE_ERROR);
		}
	}

	public void adjustStockAfterSale(int product_id, int quantity) throws BusinessException {
		quantity = quantity * (-1);
		ProductService.getInstance().updateStock(product_id, quantity);
	}

}
