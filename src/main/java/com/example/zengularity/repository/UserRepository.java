package com.example.zengularity.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import com.example.zengularity.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public default User create(User u) throws ResponseStatusException {
        this.findById(u.getUsername()).map(user -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Le nom d'utilisateur est déjà pris");
        });

        if (u.getUsername() == null || u.getPassword() == null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Il est nécessaire d'indiquer un username et un password");
        return this.save(u);
    }

    public default String authentification(User u) throws ResponseStatusException {
        ResponseStatusException error =  new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Le nom d'utilisateur ou le mot de passe est incorrecte");
        return this.findById(u.getUsername()).map(user -> {
            if (!user.getPassword().equals(u.getPassword()))
                throw error;
            String privatekey = UUID.randomUUID().toString();
            this.save(new User(user.getUsername(),user.getPassword(),privatekey));
            return privatekey;
        }).orElseThrow(() -> error);
    }
    
    public default boolean isAuth(String username, String privatekey){
        if(username == null || privatekey == null) return false;
        return this.findById(username).map(user -> {
            if(user.getPrivatekey().equals(privatekey))
                return true;
            else {
                return false;
            }
        }).orElse(false);
    }
}
