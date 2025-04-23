package DAO;

import java.util.List;
import java.util.Optional;

// Base Data Access Object (DAO) interface to define common CRUD operations.
// This interface is intended to be implemented by DAOs for specific models.

public interface FirstDao<T> {
    // Retrieves an entity by its unique ID.
    Optional<T> getById(int id);

    //Retrieves all entities from the data source.
    List<T> getAll();

    // Inserts a new entity into the data source.
    T insert(T t);

    // Updates an existing entity in the data source.
    boolean update(T t);

    // Deletes an entity from the data source.
    boolean delete(T t);
}