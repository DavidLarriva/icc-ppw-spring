package ec.edu.ups.icc.fundamentos01.users.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;

@RestController
@RequestMapping("/users")
public class UserController {

    private final List<UserModel> users = new ArrayList<>();
    private final UserMapper userMapper = new UserMapper();
    private long currentId = 1;

    // GET /users - lista todos los usuarios
    @GetMapping
    public List<UserResponseDto> getAll() {
        return users.stream().map(userMapper::toResponse).toList();
    }

    // GET /users/{id} - un usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id) {
        UserModel user = findById(id);
        if (user == null) {
            return notFound(id);
        }
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    // POST /users - crea un usuario (el id se genera automáticamente)
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody CreateUserDto dto) {
        UserModel user = userMapper.toModel(dto);
        user.setId(currentId++);
        users.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(user));
    }

    // PUT /users/{id} - reemplaza los datos del usuario
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody UpdateUserDto dto) {
        UserModel user = findById(id);
        if (user == null) {
            return notFound(id);
        }
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    
    @PatchMapping("/{id}")
    public ResponseEntity<Object> partialUpdate(@PathVariable long id, @RequestBody PartialUpdateUserDto dto) {
        UserModel user = findById(id);
        if (user == null) {
            return notFound(id);
        }
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            user.setPassword(dto.getPassword());
        }
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        UserModel user = findById(id);
        if (user == null) {
            return notFound(id);
        }
        users.remove(user);
        return ResponseEntity.noContent().build();
    }

    private UserModel findById(long id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    private ResponseEntity<Object> notFound(long id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Usuario no encontrado con id " + id));
    }
}
