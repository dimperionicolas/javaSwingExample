package main.java.nicodim.pharmacy.services;

import java.util.List;

import main.java.nicodim.pharmacy.dao.ProductsDao;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.exceptions.ErrorCode;
import main.java.nicodim.pharmacy.exceptions.ValidationException;
import main.java.nicodim.pharmacy.models.Products;
import main.java.nicodim.pharmacy.services.base.BaseService;

public class ProductService extends BaseService {

	private final ProductsDao productDao;
	private static volatile ProductService instance;

	private ProductService() {
		this.productDao = new ProductsDao();
	}

	public static ProductService getInstance() {
		if (instance == null) {
			synchronized (ProductService.class) {
				if (instance == null) {
					instance = new ProductService();
				}
			}
		}
		return instance;
	}

	public void registerProduct(Products product) throws BusinessException {
		validateProduct(product);
		if (productDao.exists(product.getId())) {
			throw new BusinessException("Ya existe un producto con el ID " + product.getId(),
					ErrorCode.DUPLICATE_ENTITY);
		}
		if (!productDao.registerProductQuery(product)) {
			throw new BusinessException("Error al registrar el cliente en la base de datos", ErrorCode.DATABASE_ERROR);
		}
	}

	public void updateProduct(Products product) throws BusinessException {
		validateProduct(product);
		if (!productDao.updateProductQuery(product)) {
			throw new BusinessException("Ha ocurrido un error al modificar los datos del producto",
					ErrorCode.DATABASE_ERROR);
		}
	}

	public void deleteProduct(int id) throws BusinessException {
		if (!productDao.deleteProductQuery(id)) {
			throw new BusinessException("Ha ocurrido un error al eliminar el producto", ErrorCode.DATABASE_ERROR);
		}
	}

	@Override
	public List<?> listAllElements(String text) throws BusinessException {
		List<Products> list = productDao.listProductsQuery(text);
		if (list == null) {
			throw new BusinessException("Ha ocurrido un error al listar los productos", ErrorCode.DATABASE_ERROR);
		}
		return list;
	}

	public Products searchProduct(int id) throws BusinessException {
		Products product = productDao.searchProduct(id);
		if (product == null) {
			throw new BusinessException("Ha ocurrido un error al listar los productos", ErrorCode.DATABASE_ERROR);
		}
		return product;

	}

	private void validateProduct(Products product) throws ValidationException {
		if (product == null) {
			throw new ValidationException("El Producto no puede ser nulo");
		}
	}

	public void updateStock(int product_id, int quantity) throws BusinessException {
		Products product = productDao.searchId(product_id);
		if (product == null) {
			throw new ValidationException("El producto no puede ser nulo");
		}

		int amount = product.getProduct_quantity() + quantity;
		if (!productDao.updateStockQuery(amount, product_id)) {
			throw new BusinessException("Ha ocurrido un error al actualizar el producto.", ErrorCode.DATABASE_ERROR);
		}
	}

	public Products searchCode(int id) throws ValidationException {
		Products product = productDao.searchCode(id);
		if (product == null) {
			throw new ValidationException("No se encontro un producto con ese Id.");
		}
		return product;
	}

}
