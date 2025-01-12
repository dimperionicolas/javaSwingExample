package main.java.nicodim.pharmacy.services;

import java.util.List;

import main.java.nicodim.pharmacy.dao.CategoriesDao;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.exceptions.ErrorCode;
import main.java.nicodim.pharmacy.exceptions.ValidationException;
import main.java.nicodim.pharmacy.models.Categories;
import main.java.nicodim.pharmacy.services.base.BaseService;

public class CategoryService extends BaseService {

	private final CategoriesDao categoryDao;
	private static volatile CategoryService instance;

	private CategoryService() {
		this.categoryDao = new CategoriesDao();
	}

	public static CategoryService getInstance() {
		if (instance == null) {
			synchronized (CategoryService.class) {
				if (instance == null) {
					instance = new CategoryService();
				}
			}
		}
		return instance;
	}

	public void registerCategory(Categories category) throws BusinessException {
		validateCategory(category);
		if (categoryDao.exists(category.getId())) {
			throw new BusinessException("Ya existe una categoria con el ID " + category.getId(),
					ErrorCode.DUPLICATE_ENTITY);
		}
		if (!categoryDao.registerCategoryQuery(category)) {
			throw new BusinessException("Error al registrar la categoría en la base de datos.",
					ErrorCode.DATABASE_ERROR);
		}
	}

	public void updateCategory(Categories category) throws BusinessException {
		validateCategory(category);
		if (!categoryDao.updateCategoryQuery(category)) {
			throw new BusinessException("Ha ocurrido un error al modificar los datos de la categoría.",
					ErrorCode.DATABASE_ERROR);
		}
	}

	public void deleteCategory(int id) throws BusinessException {
		if (!categoryDao.deleteCategoryQuery(id)) {
			throw new BusinessException("Ha ocurrido un error al eliminar la categoría.", ErrorCode.DATABASE_ERROR);
		}
	}

	private void validateCategory(Categories category) throws ValidationException {
		if (category == null) {
			throw new ValidationException("La categoría no puede ser nula");
		}
		if (category.getName() == null || category.getName().trim().isEmpty()) {
			throw new ValidationException("El nombre es requerido");
		}
	}

	@Override
	public List<?> listAllElements(String text) throws BusinessException {
		List<Categories> list = categoryDao.listCategoriesQuery(text);
		if (list == null) {
			throw new BusinessException("Ha ocurrido un error al listar las categorias", ErrorCode.DATABASE_ERROR);
		}
		return list;
	}
}
