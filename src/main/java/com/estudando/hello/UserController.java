package com.estudando.hello;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    // LISTAR
    @GetMapping
    public List<User> list() {
        return repo.findAll();
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CRIAR
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User body) {
        if (body.getId() != null)
            body.setId(null);
        // regra simples: e-mail único
        if (body.getEmail() != null && repo.existsByEmail(body.getEmail())) {
            return ResponseEntity.unprocessableEntity().build();
        }
        User saved = repo.save(body);
        return ResponseEntity
                .created(URI.create("/api/users/" + saved.getId()))
                .body(saved);
    }

    // ATUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User body) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setName(body.getName());
                    existing.setEmail(body.getEmail());
                    return ResponseEntity.ok(repo.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // EXCLUIR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id))
            return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
