package com.example.zengularity.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.zengularity.model.Centrale;
import com.example.zengularity.model.CentraleKind;
import com.example.zengularity.model.User;
import com.example.zengularity.repository.CentraleRepository;
import com.example.zengularity.repository.UserRepository;

@RestController
public class CentraleController {

    @Autowired
    private CentraleRepository centraleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/centrale/findall")
    public List<Centrale> getUserCentrales(
            @CookieValue(value = UserController.COOKIE_PROP_USER, required = false) String username,
            @CookieValue(value = UserController.COOKIE_PROP_KEY, required = false) String key) {
        User user = this.getCurrentUser(username, key);
        return this.findUserCentrales(user);
    }

    @GetMapping("/centrale/stockages")
    public Map<String, Double> getUserStockageCentrales(
            @CookieValue(value = UserController.COOKIE_PROP_USER, required = false) String username,
            @CookieValue(value = UserController.COOKIE_PROP_KEY, required = false) String key) {
        User user = this.getCurrentUser(username, key);
        List<Centrale> centrales = this.findUserCentrales(user);
        HashMap<String, Double> map = new HashMap<>();
        for (Centrale c : centrales) {
            map.put(c.getName(), c.getStockage());
        }
        return map;
    }

    @GetMapping("/centrale/stockages/{kind}")
    public Map<String, Double> getUserStockageCentralesByKind(@PathVariable CentraleKind kind,
            @CookieValue(value = UserController.COOKIE_PROP_USER, required = false) String username,
            @CookieValue(value = UserController.COOKIE_PROP_KEY, required = false) String key) {
        User user = this.getCurrentUser(username, key);
        List<Centrale> centrales = this.findUserCentrales(user);
        HashMap<String, Double> map = new HashMap<>();
        for (Centrale c : centrales) {
            if (c.getKind().equals(kind)) {
                map.put(c.getName() + " production :", c.getProducted());
                map.put(c.getName() + " consommation :", c.getConsumed());
            }
        }
        return map;
    }

    @GetMapping("/centrale/overview/")
    public Map<String, Double> getUserCentralesOverview(
            @CookieValue(value = UserController.COOKIE_PROP_USER, required = false) String username,
            @CookieValue(value = UserController.COOKIE_PROP_KEY, required = false) String key) {
        User user = this.getCurrentUser(username, key);
        List<Centrale> centrales = this.findUserCentrales(user);
        HashMap<String, Double> map = new HashMap<>();
        Double consommation = 0.0;
        Double production = 0.0;
        for (Centrale c : centrales) {
            consommation += c.getConsumed();
            production += c.getProducted();
        }
        map.put("Production globale :", production);
        map.put("Consommation globale:", consommation);
        return map;
    }

    @PostMapping("/centrale")
    public Centrale createCentrale(@Valid @RequestBody Centrale centrale,
            @CookieValue(value = UserController.COOKIE_PROP_USER, required = false) String username,
            @CookieValue(value = UserController.COOKIE_PROP_KEY, required = false) String key) {
        User user = this.getCurrentUser(username, key);
        return centraleRepository.create(new Centrale(centrale.getName(), centrale.getKind(),
                centrale.getStockageMax(), user));
    }

    @PutMapping("/centrale/{centralename}/production/{producted}")
    public Centrale productEnergy(@PathVariable String centralename, @PathVariable Double producted,
            @CookieValue(value = UserController.COOKIE_PROP_USER, required = false) String username,
            @CookieValue(value = UserController.COOKIE_PROP_KEY, required = false) String key) {
        User user = this.getCurrentUser(username, key);
        Centrale centrale = findCentraleByName(user, centralename);
        if (centrale.getStockage() + producted > centrale.getStockageMax())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Centrale " + centrale.getName()
                    + " | Stockage max " + centrale.getStockageMax() + " < Stockage " + centrale.getStockage()
                    + " + Production " + producted);

        return centraleRepository.save(new Centrale(centrale.getId(), centrale.getName(), centrale.getKind(), centrale
                .getStockage() + producted, centrale.getStockageMax(), centrale.getConsumed(), centrale.getProducted()
                + producted, centrale.getUser()));

    }

    @PutMapping("/centrale/{centralename}/consumption/{consumed}")
    public Centrale consumeEnergy(@PathVariable String centralename, @PathVariable Double consumed,
            @CookieValue(value = UserController.COOKIE_PROP_USER, required = false) String username,
            @CookieValue(value = UserController.COOKIE_PROP_KEY, required = false) String key) {
        User user = this.getCurrentUser(username, key);
        Centrale centrale = findCentraleByName(user, centralename);
        if (centrale.getStockage() - consumed < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La Centrale " + centrale.getName()
                    + " n'a pas assez d'énergie en stockage > " + centrale.getStockage());
        return centraleRepository.save(new Centrale(centrale.getId(), centrale.getName(), centrale.getKind(), centrale
                .getStockage() - consumed, centrale.getStockageMax(), centrale.getConsumed() + consumed, centrale
                .getProducted(), centrale.getUser()));
    }

    @DeleteMapping("/centrale/{centralename}")
    public ResponseEntity<?> deleteCentrale(@PathVariable String centralename,
            @CookieValue(value = UserController.COOKIE_PROP_USER, required = false) String username,
            @CookieValue(value = UserController.COOKIE_PROP_KEY, required = false) String key) {
        User user = this.getCurrentUser(username, key);
        Centrale centrale = findCentraleByName(user, centralename);
        centraleRepository.delete(centrale);
        return ResponseEntity.ok().build();
    }

    private User getCurrentUser(String username, String key) throws ResponseStatusException {
        if (userRepository.isAuth(username, key)) {
            return userRepository
                    .findById(username)
                    .map(user -> {
                        return user;
                    })
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Erreur lors de la récupération de l'utilisateur"));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erreur d'authentification");
    }

    private List<Centrale> findUserCentrales(User user) {
        Example<Centrale> example = Example.of(new Centrale(null, null, null, user));
        return centraleRepository.findAll(example);
    }

    private Centrale findCentraleByName(User user, String centralename) throws ResourceNotFoundException {
        Example<Centrale> example = Example.of(new Centrale(centralename, null, null, user));
        return centraleRepository.findOne(example).orElseThrow(
                () -> new ResourceNotFoundException("You don't have a centrale with the name " + centralename));
    }
}
