package main.java.nicodim.pharmacy.services.base;

import java.util.List;

import main.java.nicodim.pharmacy.exceptions.BusinessException;

public abstract class BaseService {

	public abstract List<?> listAllElements(String text) throws BusinessException;

}
