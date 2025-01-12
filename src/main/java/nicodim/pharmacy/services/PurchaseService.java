package main.java.nicodim.pharmacy.services;

import java.util.List;

import main.java.nicodim.pharmacy.controllers.LoginController;
import main.java.nicodim.pharmacy.dao.PurchasesDao;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.exceptions.ErrorCode;
import main.java.nicodim.pharmacy.exceptions.ValidationException;
import main.java.nicodim.pharmacy.models.Purchases;
import main.java.nicodim.pharmacy.services.base.BaseService;

public class PurchaseService extends BaseService {
	private final PurchasesDao purchasesDao;
	private int supplierId = 0;

	private static volatile PurchaseService instance;

	private PurchaseService() {
		this.purchasesDao = new PurchasesDao();
	}

	public static PurchaseService getInstance() {
		if (instance == null) {
			synchronized (PurchaseService.class) {
				if (instance == null) {
					instance = new PurchaseService();
				}
			}
		}
		return instance;
	}

	public void registerPurchase(String text) throws BusinessException {
		double total = checkAndGetPrices(text);
		int employee_id = LoginController.employee.getId();
		if (!purchasesDao.registerPurchaseQuery(supplierId, employee_id, total)) {
			throw new BusinessException("Error al registrar la compra en la base de datos", ErrorCode.DATABASE_ERROR);
		}
	}

	@Override
	public List<?> listAllElements(String text) throws BusinessException {
		List<Purchases> list = purchasesDao.listAllPurchasesQuery();
		if (list == null) {
			throw new BusinessException("Ha ocurrido un error al listar las compras", ErrorCode.DATABASE_ERROR);
		}
		return list;
	}

	public void isValidSupplier(int id) throws ValidationException {
		if (supplierId == 0) {
			supplierId = id;
		}
		if (supplierId != id) {
			throw new ValidationException("El proveedor debe ser el mismo para todos los productos de la misma compra");
		}
	}

	public int purchaseLastId() throws BusinessException {
		int id = -1;
		id = purchasesDao.purchaseId();
		if (id == -1) {
			throw new BusinessException("Ha ocurrido un error al obtener el ultimo id de compra.",
					ErrorCode.DATABASE_ERROR);
		}
		return id;
	}

	public int checkAndGetAmount(String cantidadString) throws ValidationException {
		try {
			Integer amount = Integer.parseInt(cantidadString);
			if (amount <= 0) {
				throw new ValidationException("La cantidad debe ser mayor a cero.");
			}
			return amount;
		} catch (NumberFormatException e) {
			throw new ValidationException("La cantidad no es un número válido.");
		}
	}

	public int checkAndGetId(String text) throws ValidationException {
		try {
			Integer amount = Integer.parseInt(text);
			if (amount <= 0) {
				throw new ValidationException("EL Id debe ser mayor a cero.");
			}
			return amount;
		} catch (NumberFormatException e) {
			throw new ValidationException("El Id no es un número válido.");
		}
	}

	public double checkAndGetPrices(String text) throws ValidationException {
		try {
			double price = Double.parseDouble(text);
			if (price <= 0) {
				throw new ValidationException("EL precio debe ser mayor a cero.");
			}
			return price;
		} catch (NumberFormatException e) {
			throw new ValidationException("El precio no es un número válido.");
		}
	}

	public void registerPurchaseDetailQuery(int purchase_id, double purchase_price, int purchase_amount,
			double purchase_subtotal, int product_id) throws BusinessException {
		if (!purchasesDao.registerPurchaseDetailQuery(purchase_id, purchase_price, purchase_amount, purchase_subtotal,
				product_id)) {
			throw new BusinessException("Ha ocurrido un error al registrar los detalles de compra.",
					ErrorCode.DATABASE_ERROR);
		}

	}

	public void clearSupplier() {
		supplierId = 0;
	}

	public void adjustStockAfterPurchase(int product_id, int purchase_amount) throws BusinessException {
		ProductService.getInstance().updateStock(product_id, purchase_amount);
	}
}
