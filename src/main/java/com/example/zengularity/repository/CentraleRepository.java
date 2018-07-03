package com.example.zengularity.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import com.example.zengularity.model.Centrale;

@Repository
public interface CentraleRepository extends JpaRepository<Centrale, Long>{
    public default Centrale create(Centrale c) throws ResponseStatusException{
        if (c.getId() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il n'est pas possible d'indiquer d'Id lors de la création");
        if (c.getKind() == null || c.getName() == null || c.getStockageMax() == null || c.getUser()==null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Il est nécessaire d'indiquer un nom, un genre, et un stockage pour la centrale");
        return this.save(new Centrale(c.getName(), c.getKind(), c.getStockageMax(),c.getUser()));
    }
}
